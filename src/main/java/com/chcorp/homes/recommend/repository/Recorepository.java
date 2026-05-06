package com.chcorp.homes.recommend.repository;

import com.chcorp.homes.recommend.entity.Recoentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Recorepository extends JpaRepository<Recoentity, Long> {
    List<Recoentity> findByActiveTrue();
}
