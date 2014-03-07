package com.example.drexelcabapp;

import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.api.services.calendar.model.Event;

public class EventComparer implements Comparator<Event> {

	@Override
	public int compare(Event arg0, Event arg1) {
		if(arg0.getStart().getDateTime() == null){
			return -1;
		}else if(arg1.getStart().getDateTime() == null){
			return 1;
		}
		
		if(arg0.getStart().getDateTime().getValue() < arg1.getStart().getDateTime().getValue()){
			return -1;
		}else if(arg0.getStart().getDateTime().getValue() == arg1.getStart().getDateTime().getValue()){
			return 0;
		}else{
			return 1;
		}
	}

}