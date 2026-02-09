package com.cs.cs.repository;


import com.cs.cs.model.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WindowRepository extends JpaRepository<Window, Long> {
    List<Window> findByServiceId(Long serviceId);
    List<Window> findByStatus(String status);
}