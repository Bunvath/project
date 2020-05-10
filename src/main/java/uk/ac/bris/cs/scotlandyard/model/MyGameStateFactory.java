package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull
	@Override
	public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
		List<Player> AllPlayers = new ArrayList<>(detectives);
		AllPlayers.add(mrX);
		final class MyGameState implements GameState {

			private GameSetup setup;
			private ImmutableSet<Piece> remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detective;
			private List<Player> everyone = AllPlayers;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner = ImmutableSet.of();
			int counter = 0;


			private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log,
								final Player mrX, final List<Player> detective) {
				this.setup = setup;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detective = detective;
				if (setup == null || remaining == null || log == null || mrX == null || detective == null)
					throw new NullPointerException();
				if (remaining.isEmpty() || detective.isEmpty() || setup.rounds.isEmpty())
					throw new IllegalArgumentException();
				for (final var det : this.detective) {
					if (det.has(ScotlandYard.Ticket.DOUBLE) || det.has(ScotlandYard.Ticket.SECRET)) {
						throw new IllegalArgumentException();
					}
				}
				if (everyone.isEmpty() || (setup.graph.nodes().isEmpty())) throw new IllegalArgumentException();
				for (Player det : this.detective) {
					for (Player dets : this.detective) {
						if (dets != det) {
							if (det.location() == dets.location()) throw new IllegalArgumentException();
						}
					}
				}
			}

			@Override
			public GameSetup getSetup() {
				return setup;
			}

			@Override
			public ImmutableList<LogEntry> getMrXTravelLog() {
				return log;
			}

			@Override
			public ImmutableSet<Piece> getPlayers() {
				Set<Piece> players = new HashSet<>();
				for (Player v : everyone) {
					players.add(v.piece());
				}
				return ImmutableSet.copyOf(players);
			}

			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
				for (final var p : this.detective) {
					if (p.piece() == detective) return Optional.of(p.location());
				}
				return Optional.empty();
			}

			@Override
			public Optional<TicketBoard> getPlayerTickets(Piece piece) {
				Player currentPlayer = null;
				for (Player v : everyone) {
					if (v.piece() == piece) currentPlayer = v;
				}
				if (everyone.contains(currentPlayer)) {
					if (currentPlayer.isMrX()) {
						TicketBoard mrx = (ticket) -> {
							if (ticket == ScotlandYard.Ticket.TAXI) return 1;
							if (ticket == ScotlandYard.Ticket.BUS) return 2;
							if (ticket == ScotlandYard.Ticket.UNDERGROUND) return 3;
							if (ticket == ScotlandYard.Ticket.DOUBLE) return 4;
							if (ticket == ScotlandYard.Ticket.SECRET) return 5;
							else return -1;
						};
						return Optional.of(mrx);
					}
					if (currentPlayer.isDetective()) {
						TicketBoard player = (ticket) -> {
							if (ticket == ScotlandYard.Ticket.TAXI) return 5;
							if (ticket == ScotlandYard.Ticket.BUS) return 4;
							if (ticket == ScotlandYard.Ticket.UNDERGROUND) return 3;
							if (ticket == ScotlandYard.Ticket.SECRET) return 0;
							if (ticket == ScotlandYard.Ticket.DOUBLE) return 0;
							else return -1;
						};
						return Optional.of(player);
					}
				}
				return Optional.empty();
			}

			@Override
			public ImmutableSet<Piece> getWinner() {
				return winner;
			}

			@Override
			public ImmutableSet<Move> getAvailableMoves() {
				HashSet<Move> Moves = new HashSet<>();
				for (Player detective : this.detective) {
					if (remaining.contains(detective.piece()) && (counter < setup.rounds.size()) ) {
						if(counter < setup.rounds.size()) Moves.addAll(makeSingleMoves(setup, this.detective, detective, detective.location()));
					}
				}
				if (remaining.contains(mrX.piece())) {
					if(counter < setup.rounds.size()) Moves.addAll(makeSingleMoves(setup, this.detective, mrX, mrX.location()));
					if(counter < setup.rounds.size() - 1) Moves.addAll(makeDoubleMoves(setup, this.detective, mrX, mrX.location()));
				}
				return ImmutableSet.copyOf(Moves);
			}

			@Override
			public GameState advance(Move move) {
				List<LogEntry> newLog = getMrXTravelLog();
				if (!getAvailableMoves().contains(move)) throw new IllegalArgumentException("Illegal move: " + move);
				ImmutableSet<Piece> newRemaining = ImmutableSet.copyOf(updateLocationAndTickets(move));
				if(move.commencedBy().isMrX()) newLog = updateLog(move);
				return new MyGameState(setup, newRemaining, ImmutableList.copyOf(newLog), mrX, detective);
			}

			public Set<Piece> updateLocationAndTickets (Move move){
				Player currentPlayer = null;
				for (Player v : everyone) {
					if (move.commencedBy() == v.piece()) currentPlayer = v;
				}
				Integer location = move.visit(new Move.Visitor<>() {
					@Override
					public Integer visit(Move.SingleMove move) {
						return move.destination;
					}

					@Override
					public Integer visit(Move.DoubleMove move) {
						return move.destination2;
					}
				});
				currentPlayer = currentPlayer.use(move.tickets());
				currentPlayer = currentPlayer.at(location);
				if(currentPlayer.piece().isDetective()) mrX = mrX.give(move.tickets());
				Set<Piece> newRemaining = new HashSet<>();
				for(Piece piece : remaining){
					if (piece != currentPlayer.piece()) newRemaining.add(piece);
				}
				if (newRemaining.isEmpty()) {
					if (currentPlayer.isDetective()) newRemaining.add(mrX.piece());
					else for (Player player : detective) {
						newRemaining.add(player.piece());
					}
				}
				return newRemaining;
			}

			public ImmutableList<LogEntry> updateLog(Move move) {
				Set<LogEntry> newLog = new HashSet<>(Set.copyOf(getMrXTravelLog()));
				ArrayList<Integer> destinations = new ArrayList<>();
				ArrayList<Integer> destination = move.visit(new Move.Visitor<>() {
					@Override
					public ArrayList<Integer> visit(Move.SingleMove move) {
						destinations.add(0, move.destination);
						return destinations;
					}

					@Override
					public ArrayList<Integer> visit(Move.DoubleMove move) {
						destinations.add(0, move.destination1);
						destinations.add(1, move.destination2);
						return destinations;
					}
				});
				int index = 0;
				for (ScotlandYard.Ticket ticketUsed : move.tickets()){
					if (ticketUsed != ScotlandYard.Ticket.DOUBLE) {
						if (setup.rounds.get(counter)) newLog.add(LogEntry.reveal(ticketUsed, destination.get(index)));
						else newLog.add(LogEntry.hidden(ticketUsed));
						counter =+ 1;
						index =+ 1;
					}
				}
				return ImmutableList.copyOf(newLog);
			}
		}
		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}
	private static ImmutableSet<Move.SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
		final var singleMoves = new HashSet<Move.SingleMove>();
		for (int destination : setup.graph.adjacentNodes(source)) {
			var occupied = false;
			for (Player det : detectives) {
				if (det.location() == destination) {
					occupied = true;
					break;
				}
			}
			if (occupied) continue;
			for (ScotlandYard.Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
				if (player.has(ScotlandYard.Ticket.SECRET))
					singleMoves.add(new Move.SingleMove(player.piece(), source, ScotlandYard.Ticket.SECRET, destination));
				if (player.has(t.requiredTicket()))
					singleMoves.add(new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination));
			}
		}
		return ImmutableSet.copyOf(singleMoves);
	}

	private static ImmutableSet<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
		if (!(player.has(ScotlandYard.Ticket.DOUBLE))) {
			HashSet<Move.DoubleMove> empty = new HashSet<>();
			return ImmutableSet.copyOf(empty);
		}
		else {
			ImmutableSet<Move.SingleMove> firstMove = makeSingleMoves(setup, detectives, player, source);
			final var doubleMoves = new HashSet<Move.DoubleMove>();
			for (Move.SingleMove move : firstMove) {
				Player player2 = player;
				player2 = player2.use(move.ticket);
				ImmutableSet<Move.SingleMove> secondMove = makeSingleMoves(setup, detectives, player2, move.destination);
				for (Move.SingleMove second : secondMove) {
					var contains = false;
					for (Move.DoubleMove doubleMove : doubleMoves) {
						contains = false;
						if (doubleMove.destination1 == move.destination && doubleMove.destination2 == second.destination
								&& doubleMove.ticket1 == move.ticket && doubleMove.ticket2 == second.ticket)
							contains = true;
					}
					if (!contains)
						doubleMoves.add(new Move.DoubleMove(player.piece(), source, move.ticket, move.destination, second.ticket, second.destination));
				}
			}
			return ImmutableSet.copyOf(doubleMoves);
		}
	}
}