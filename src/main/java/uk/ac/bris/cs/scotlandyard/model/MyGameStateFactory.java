package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.List;
import java.util.Optional;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
		final class MyGameState implements GameState{

			private GameSetup setup;
			private ImmutableSet<Piece> remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detective;
			private List<Player> everyone;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner = null;

			private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detective) {
				this.setup = setup;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detective = detective;
				if(setup == null) throw new NullPointerException();
				if(remaining == null) throw new NullPointerException();
				if(log == null) throw new NullPointerException();
				if(mrX == null) throw new NullPointerException();
				if(detective == null) throw new NullPointerException();
				if(remaining.isEmpty()) throw new IllegalArgumentException();
				if(detective.isEmpty()) throw new IllegalArgumentException();
				if (setup.rounds.isEmpty()) throw new IllegalArgumentException();
				for(final var det : detectives){
					if(det.has(ScotlandYard.Ticket.DOUBLE) || det.has(ScotlandYard.Ticket.SECRET)){
						throw new IllegalArgumentException();
					}
				}
				if(mrX.has(Ticket.BUS) || mrX.has(Ticket.TAXI) || mrX.has(Ticket.UNDERGROUND)) {
					throw new IllegalArgumentException();
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
				for(final var v : everyone){
					if (v.isDetective() == true ){
						return remaining.of(v.piece());
					}
				}
				return remaining;
			}
			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective){
				for (final var p : detectives) {
					if (p.piece() == detective) return Optional.of(p.location());
				}
				return Optional.empty();

			}
			@Override
			public  Optional<TicketBoard> getPlayerTickets(Piece piece){
				for (final var p : detectives) {
					if (p.piece() == piece) ;
				}
				return Optional.of(class PlayerTicketBoard implements TicketBoard {
					@Override
					public int getCount(@Nonnull Ticket ticket) {
						return ticket;
					}
				}
				return Optional.empty();
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
		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}

}