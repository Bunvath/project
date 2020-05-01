package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.*;
import com.google.common.io.Resources;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.readGraph;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
		List<Player> AllPlayers = new ArrayList<Player>();
		for(Player players: detectives){
			AllPlayers.add(players);
		}
		AllPlayers.add(mrX);
		final class MyGameState implements GameState{

			private GameSetup setup;
			private ImmutableSet<Piece> remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detective;
			private List<Player> everyone = AllPlayers;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner = ImmutableSet.of();


			private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log,
								final Player mrX, final List<Player> detective) {
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
				if(setup.rounds.isEmpty()) throw new IllegalArgumentException();
				if(setup.graph == null) throw new NullPointerException();
				for(final var det : detectives){
					if(det.has(ScotlandYard.Ticket.DOUBLE) || det.has(ScotlandYard.Ticket.SECRET) || det == null){
						throw new IllegalArgumentException();
					}
				}
				if (everyone.isEmpty() == true) throw new IllegalArgumentException();
				if (setup.graph.nodes().isEmpty()) throw new IllegalArgumentException();
			}

			@Override
			public GameSetup getSetup() {
				/*Array firstTaxi = (1 ,1 ,2 ,2 ,3 ,3 ,3 ,4 ,5 ,5 ,6 ,6 ,7 ,8 ,8 ,9 ,9 ,10 ,10 ,10 ,11 ,12 ,13 ,13 ,
						14 ,14 ,15 ,15 ,15 ,16 ,16 ,17 ,17 ,18 ,18 ,19 ,20 ,21 ,22 ,22 ,22 ,23 ,24 ,24 ,25 ,25 ,26 ,26 ,
						27 ,27 ,28 ,29 ,29 ,30 ,31 ,31 ,32 ,32 ,32 ,33 ,34 ,34 ,35 ,35 ,35 ,36 ,36 ,37 ,38 ,38 ,39 ,39 ,
						40 ,40 ,40 ,41 ,42 ,42 ,43 ,44 ,45 ,45 ,45 ,45 ,46 ,46 ,47 ,48 ,48 ,49 ,49 ,51 ,51 ,51 ,52 ,53 ,
						53 ,54 ,54 ,55 ,56 ,57 ,57 ,58 ,58 ,58 ,59 ,59 ,60 ,60 ,61 ,61 ,61 ,62 ,63 ,63 ,63 ,64 ,64 ,65 ,
						65 ,66 ,66 ,67 ,67 ,68 ,68 ,69 ,70 ,70 ,71 ,71 ,72 ,72 ,73 ,73 ,74 ,74 ,75 ,76 ,77 ,77 ,77 ,78 ,
						78 ,79 ,80 ,80 ,81 ,81 ,82 ,83 ,83 ,84 ,85 ,86 ,86 ,87 ,88 ,88 ,89 ,90 ,90 ,91 ,91 ,92 ,93 ,94 ,
						95 ,96 ,96 ,97 ,97 ,98 ,98 ,99 ,99 ,100 ,100 ,100 ,101 ,102 ,102 ,104 ,105 ,105 ,106 ,107 ,108 ,
						108 ,109 ,109 ,110 ,111 ,111 ,112 ,113 ,113 ,114 ,114 ,114 ,114 ,115 ,115 ,116 ,116 ,116 ,117 ,
						118 ,118 ,118 ,119 ,120 ,120 ,121 ,121 ,122 ,122 ,123 ,123 ,123 ,123 ,124 ,124 ,125 ,126 ,126 ,
						127 ,127 ,128 ,129 ,129 ,129 ,130 ,130 ,132 ,133 ,133 ,134 ,134 ,135 ,135 ,135 ,136 ,137 ,138 ,
						138 ,139 ,139 ,139 ,140 ,140 ,141 ,141 ,142 ,142 ,142 ,143 ,143 ,144 ,144 ,145 ,146 ,146 ,147 ,
						148 ,148 ,149 ,149 ,150 ,151 ,151 ,151 ,152 ,153 ,153 ,153 ,154 ,155 ,155 ,155 ,156 ,156 ,157 ,
						157 ,158 ,159 ,159 ,159 ,159 ,160 ,160 ,160 ,161 ,162 ,163 ,164 ,164 ,165 ,165 ,166 ,166 ,167 ,
						167 ,168 ,169 ,170 ,171 ,171 ,171 ,172 ,172 ,173 ,173 ,174 ,176 ,176 ,178 ,178 ,179 ,180 ,180 ,
						181 ,181 ,182 ,182 ,183 ,184 ,184 ,184 ,185 ,186 ,187 ,187 ,188 ,189 ,190 ,190 ,191 ,192 ,193 ,
						194 ,195 ,196 ,198);

				Array secondTaxi= (8, 9, 10, 20, 4 ,11 ,12 ,13 ,15 ,16 ,7 ,29 ,17 ,18 ,19 ,19 ,20 ,11 ,21 ,34 ,
						22 ,23, 23 ,24 ,15 ,25 ,16 ,26 ,28 ,28 ,29 ,29 ,30 ,31 ,43 ,32 ,33 ,33 ,23 ,34 ,35 ,37 ,37 ,38 ,
						38 ,39 ,27, 39 ,28 ,40 ,41 ,41 ,42 ,42 ,43 ,44 ,33 ,44 ,45 ,46 ,47 ,48 ,36 ,48 ,65 ,37 ,49 ,50 ,
						50 ,51 ,51 ,52, 41 ,52 ,53 ,54 ,56 ,72 ,57 ,58 ,46 ,58 ,59 ,60 ,47 ,61 ,62 ,62 ,63 ,50 ,66 ,52 ,
						67 ,68 ,69 ,54 ,69, 55 ,70 ,71 ,91 ,58 ,73 ,59 ,74 ,75 ,75 ,76 ,61 ,76 ,62 ,76 ,78 ,79 ,64 ,79 ,
						80 ,65 ,81 ,66 ,82 ,67, 82 ,68 ,84 ,69 ,85 ,86 ,71 ,87 ,72 ,89 ,90 ,91 ,74 ,92 ,75 ,92 ,94 ,77 ,
						78 ,95 ,96 ,79 ,97 ,98 ,99, 100 ,82 ,100 ,101 ,101 ,102 ,85 ,103 ,103 ,104 ,88 ,89 ,117 ,105 ,
						91 ,105 ,105 ,107 ,93 ,94 ,95 ,122,	97 ,109 ,98 ,109 ,99 ,110 ,110 ,112 ,101 ,112 ,113 ,114 ,
						103 ,115 ,116 ,106 ,108 ,107 ,119 ,117 ,119,110 ,124 ,111 ,112 ,124 ,125 ,114 ,125 ,115, 126 ,
						131 ,132 ,126 ,127 ,117 ,118 ,127 ,129 ,129 ,134, 142 ,136 ,121 ,144 ,122 ,145 ,123 ,146 ,124 ,
						137 ,148 ,149 ,130 ,138 ,131 ,127 ,140 ,133 ,134 ,188, 135 ,142 ,143 ,131 ,139 ,140 ,140 ,141 ,
						141 ,142 ,136 ,143 ,161 ,162 ,147 ,150 ,152 ,140 ,153 ,154, 154 ,156 ,142 ,158 ,128 ,143 ,158 ,
						128 ,160 ,145 ,177 ,146 ,147 ,163 ,164 ,149 ,164 ,150 ,165 ,151, 152 ,165 ,166 ,153 ,154 ,166 ,
						167 ,155 ,156 ,167 ,168 ,157 ,169 ,158 ,170 ,159 ,170 ,172 ,186 ,198, 128 ,161 ,173 ,174 ,175 ,
						177 ,178 ,179 ,179 ,180 ,181 ,183 ,168 ,183 ,184 ,184 ,185 ,173 ,175 ,199, 128 ,187 ,174 ,188 ,
						175 ,177 ,189 ,189 ,191 ,191 ,181 ,193 ,182 ,193 ,183 ,195 ,196 ,185 ,196 ,197, 186 ,198 ,188 ,
						198 ,199 ,190, 191, 192 ,192 ,194 ,194 ,195 ,197 ,197 ,199);
				Array firstBus = (1 ,1 ,3 ,3 ,7 ,13 ,13 ,13 ,14 ,15 ,15 ,22 ,22 ,22 ,23 ,29 ,29 ,29 ,34 ,34 ,41 ,
						41 ,42 ,46 ,46 ,52 ,52 ,55 ,58 ,58 ,63 ,63 ,63 ,65 ,65 ,67 ,67 ,72 ,72 ,74 ,77 ,77 ,77 ,78 ,82 ,
						82 ,86 ,86 ,86 ,87 ,89 ,93 ,100 ,102 ,105 ,105 ,107 ,108 ,108 ,111 ,116 ,116 ,122 ,122 ,123 ,
						123 ,123 ,124 ,127 ,128 ,128 ,133 ,133 ,135 ,135 ,140 ,140 ,142 ,142 ,144 ,153 ,153 ,153 ,154 ,
						156 ,156 ,157 ,161 ,161 ,163 ,163 ,165 ,165 ,176 ,180 ,180 ,184 ,185 ,190);
				Array secondBus = (46 ,58 ,22 ,23 ,42 ,14 ,23 ,52 ,15 ,29 ,41 ,34 ,23 ,65 ,67 ,41 ,42 ,55 ,46 ,
						63 ,52 ,87 ,72 ,58 ,78 ,67 ,86 ,89 ,74 ,77 ,65 ,79 ,100 ,67 ,82 ,82 ,102 ,105 ,107 ,94 ,78 ,94 ,
						124 ,79 ,100 ,140 ,87 ,102 ,116 ,105 ,105 ,94 ,111 ,127 ,107 ,108 ,161 ,116 ,135 ,124 ,127 ,
						142 ,123 ,144 ,124 ,144 ,165 ,153 ,133 ,187 ,199 ,140 ,157 ,128 ,161 ,154 ,156 ,128 ,157 ,163 ,
						154 ,180 ,184 ,156 ,157 ,184 ,185 ,128 ,199 ,176 ,191 ,180 ,191 ,190 ,184 ,190 ,185 ,187 ,191);
				Array firstUnder = (1 ,13 ,13 ,13 ,46 ,46 ,67 ,67 ,67 ,79 ,79 ,89 ,89 ,111 ,111 ,128 ,140 ,140 ,
						153 ,153);
				Array secondUnder = (46,46 ,67 ,89 ,74 ,79 ,79 ,89 ,111 ,93 ,111 ,128 ,140 ,153 ,163 ,185 ,128 ,
						153 ,163 ,185);
				Array firstFerry =  (194, 157, 115);
				public static void setUp() {
					} catch (IOException e) { throw new RuntimeException("Unable to read game graph", e); }
					try {
						defaultGraph = readGraph(Resources.toString(Resources.getResource(
								"graph.txt"),
								StandardCharsets.UTF_8));
				MutableValueGraph<Integer, Set<ScotlandYard.Transport>> map = ValueGraphBuilder.undirected().build();
				for(int x = 0; x <= 199; x++) map.addNode(x);
				for(int y = 0; y <= 344; y++) {
					if(!(map.hasEdgeConnecting(firstTaxi[y], secondTaxi[y]))) {
						Set<ScotlandYard.Transport> transport = null;
						map.putEdgeValue(firstTaxi[y], secondTaxi[y], transport.add(ScotlandYard.Transport.TAXI));
					}
				}
				for(int z = 0; z <= 98; z++) {
					if (!(map.hasEdgeConnecting(firstBus[z], secondBus[z]))) {
						Set<ScotlandYard.Transport> transport = map.edgeValue(firstBus[z], secondBus[z]);
						map.putEdgeValue(firstBus[z], secondBus[z], transport.add(ScotlandYard.Transport.BUS));
					}
				}
				for(int z = 0; z <= 19; z++) {
					if (!(map.hasEdgeConnecting(firstUnder[z], secondUnder[z]))) {
						Set<ScotlandYard.Transport> transport = map.edgeValue(firstUnder[z], secondUnder[z]);
						map.putEdgeValue(firstUnder[z], secondUnder[z], transport.add(ScotlandYard.Transport.UNDERGROUND));
					}
				}
				for(int z = 0; z <= 2; z++) {
					if (!(map.hasEdgeConnecting(firstFerry[z], secondFerry[z]))) {
						Set<ScotlandYard.Transport> transport = map.edgeValue(firstFerry[z], secondFerry[z]);
						map.putEdgeValue(firstFerry[z], secondFerry[z], transport.add(ScotlandYard.Transport.FERRY));
					}
				}*/
				/*ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph;
				try {
					graph = readGraph(Resources.toString(Resources.getResource(
							"graph.txt"),
							StandardCharsets.UTF_8));
				} catch (IOException e) { throw new RuntimeException("Unable to read game graph", e); }*/
				//GameSetup gameSetup;
				/*try {
					GameSetup gameSetup = new GameSetup(ScotlandYard.standardGraph(), setup.rounds);
					return gameSetup;
				} catch (IOException e) {throw new RuntimeException("Unable to read game graph", e); }*/
				//if (setup.graph == null) throw new NullPointerException();
				return setup;

			}

			@Override
			public ImmutableList<LogEntry> getMrXTravelLog(){
				return log;
			}

			@Override
			public ImmutableSet<Piece> getPlayers(){
				Set<Piece> players = new HashSet<Piece>();
				for(Player v : everyone){
					players.add(v.piece());
				}
				return ImmutableSet.copyOf(players);
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