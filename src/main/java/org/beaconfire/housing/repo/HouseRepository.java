package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.House;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HouseRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public void saveHouse(House house) {
        Session session = sessionFactory.getCurrentSession();
        session.save(house);
    }
}
