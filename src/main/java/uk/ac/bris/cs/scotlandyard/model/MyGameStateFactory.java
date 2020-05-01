package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.lang.instrument.UnmodifiableModuleException;
import java.util.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
		List<Player> AllPlayer = new ArrayList<>();
		AllPlayer.add(mrX);
		for(var d: detectives) {
			AllPlayer.add(d);
		};

		final class MyGameState implements GameState{

			private GameSetup setup;
			private ImmutableSet remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detective;
			private List<Player> everyone;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner;

			private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detective) {
				this.setup = setup;
				this.everyone = AllPlayer;
				this.mrX = mrX;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detective = detective;
				if(setup.graph.equals(null)) throw new IllegalArgumentException();
				if(mrX == null) throw new NullPointerException();
				for(var det : detectives){
					for(var d : detective){
						if(det.location() == d.location()) throw new IllegalArgumentException();
					}
				}

				if (setup.rounds.isEmpty()) throw new IllegalArgumentException();
				for(final var det : detectives) {
					if (det.has(ScotlandYard.Ticket.DOUBLE) || det.has(ScotlandYard.Ticket.SECRET)) {
						throw new IllegalArgumentException();
					}
				}
			}

			@Override
			public GameSetup getSetup() {
				return setup;
			}

			@Override
			public ImmutableList<LogEntry> getMrXTravelLog(){
				return log;
			}

			@Override
			public ImmutableSet<Piece> getPlayers(){
				Set<Piece> players = new HashSet();
				for(final var det : AllPlayer) {
					players.add(det.piece());
				}
				return remaining.copyOf(players);

			}
			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective){
				for (final var p : everyone) {
					if (p.piece() == detective) return Optional.of(p.location());
				}
				return Optional.empty();

			}
			@Override
			public  Optional<TicketBoard> getPlayerTickets(Piece pieces){
				return null;
			}

			@Override
			public ImmutableSet<Piece> getWinner(){
				return winner;
			}
			@Override
			public ImmutableSet<Move> getAvailableMoves(){
				return null;
			}
			@Override
			public GameState advance(Move move){
				return null;
			}
		}
		 return new MyGameState(setup, ImmutableSet.of(),ImmutableList.of(),mrX, detectives);
	}


}