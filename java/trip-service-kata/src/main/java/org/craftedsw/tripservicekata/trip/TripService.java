package org.craftedsw.tripservicekata.trip;

import java.util.ArrayList;
import java.util.List;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.IUserSession;
import org.craftedsw.tripservicekata.user.User;

public class TripService {

	private IUserSession userSession;
	private TripRepository tripDAO;

	public TripService(IUserSession userSession, TripRepository tripDAO) {
		this.userSession = userSession;
		this.tripDAO = tripDAO;
	}

	public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {

		if (this.userSession.getLoggedUser() == null) {
			throw new UserNotLoggedInException();
		}

		if (user.getFriends().contains(this.userSession.getLoggedUser())){
			return tripDAO.findTripsByUser(user);
		}

		return new ArrayList<Trip>();
	}

}
