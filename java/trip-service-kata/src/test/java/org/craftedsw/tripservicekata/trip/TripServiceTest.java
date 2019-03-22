package org.craftedsw.tripservicekata.trip;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.IUserSession;
import org.craftedsw.tripservicekata.user.User;
import org.craftedsw.tripservicekata.user.UserSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TripServiceTest {

    public static final User GUEST_USER = null;
    public static final User BOB = new User();
    public static final User DAVID = new User();
    public static final Trip TRIP_TO_LONDON = new Trip();
    public static final Trip TRIP_TO_PARIS = new Trip();

    private User charlie;

    @Before
    public void setUp() {
        charlie = new User();
    }

    @Test(expected = UserNotLoggedInException.class)
    public void should_throw_exception_when_using_a_guest_user(){
        TripService tripService = new TripService(
                new TestableUserSession(GUEST_USER),
                new TestableTripRepository(Collections.EMPTY_LIST));

        assertThat(tripService.getTripsByUser(charlie));
    }

    @Test
    public void should_return_an_empty_trip_list_when_bob_is_not_friends_with_charlie(){
        TripService tripService = new TripService(
                new TestableUserSession(BOB),
                new TestableTripRepository(Collections.EMPTY_LIST));

        assertThat(tripService.getTripsByUser(charlie)).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void should_return_charlies_trip_list_when_bob_is_a_friend(){

        TripService tripService = new TripService(
                new TestableUserSession(BOB),
                new TestableTripRepository(Arrays.asList(TRIP_TO_PARIS, TRIP_TO_LONDON)));

        charlie = new UserBuilder()
                .isFriendsWith(BOB)
                .build();

        assertThat(tripService.getTripsByUser(charlie))
                .containsExactlyInAnyOrder(TRIP_TO_LONDON, TRIP_TO_PARIS);
    }

    @Test
    public void should_return_an_empty_list_if_charlie_has_friends_but_bob_is_not(){
        TripService tripService = new TripService(
                new TestableUserSession(BOB),
                new TestableTripRepository(Arrays.asList(TRIP_TO_LONDON)));

        charlie = new UserBuilder()
                .isFriendsWith(DAVID)
                .build();

        assertThat(tripService.getTripsByUser(charlie)).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void should_return_an_empty_list_when_bob_is_a_friend_but_charlie_has_no_trips(){
        TripService tripService = new TripService(
                new TestableUserSession(BOB),
                new TestableTripRepository(Collections.EMPTY_LIST));

        charlie = new UserBuilder()
                .isFriendsWith(BOB)
                .build();

        assertThat(tripService.getTripsByUser(charlie)).isEqualTo(Collections.EMPTY_LIST);
    }

    private class TestableUserSession implements IUserSession {

        private User loggedUser;

        private TestableUserSession(User loggedUser) {
            this.loggedUser = loggedUser;
        }

        public User getLoggedUser() {
            return loggedUser;
        }
    }

    private class TestableTripRepository implements TripRepository {

        private List<Trip> tripList;

        private TestableTripRepository(List<Trip> tripList) {
            this.tripList = tripList;
        }

        public List<Trip> findTripsByUser(User user) {
            return tripList;
        }
    }

    private class UserBuilder {

        private List<User> friendList = new ArrayList<User>();
        private List<Trip> tripList = new ArrayList<Trip>();

        public UserBuilder isFriendsWith(User... friends) {
            for (User friend : friends){
                friendList.add(friend);
            }
            return this;
        }

        public UserBuilder hasTrips(Trip... trips) {
            for (Trip trip : trips){
                tripList.add(trip);
            }
            return this;
        }

        public User build() {
            User user = new User();
            for (User friend : friendList){
                user.addFriend(friend);
            }

            for (Trip trip : tripList){
                user.addTrip(trip);
            }

            return user;
        }
    }
}
