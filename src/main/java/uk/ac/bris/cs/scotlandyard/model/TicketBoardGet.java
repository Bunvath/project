package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.*;

import javax.annotation.Nonnull;

import javafx.css.converter.PaintConverter;
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

class PlayerTicketBoards implements Board.TicketBoard {
    @Override
    public int getCount(@Nonnull Ticket ticket) {
        if(ticket == Ticket.TAXI) return 1;
        if (ticket == Ticket.BUS) return 2;
        if (ticket == Ticket.UNDERGROUND) return 3;
        if(ticket == Ticket.SECRET) return 4;
        else return 0;
    }
}
public class TicketBoardGet{
    public Board.TicketBoard getTicketBoard(){
        PlayerTicketBoards ticketBoards = new PlayerTicketBoards();
        return ticketBoards;
    }
}
