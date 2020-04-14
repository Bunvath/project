package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.*;

import javax.annotation.Nonnull;

import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.*;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

public class PlayerTicketBoards implements Board.TicketBoard {
    public PlayerTicketBoards(Player player) {
        ImmutableMap<Ticket, Integer> Tickets = player.tickets();
    }
    @Override
    public int getCount(@Nonnull Ticket ticket) {
        ImmutableMap<Ticket, Integer> Tickets = player.tickets()
        player.tickets().get(ticket);
        Tickets.get(ticket);
    }
}
