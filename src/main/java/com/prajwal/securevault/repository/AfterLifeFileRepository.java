package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.AfterLifeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AfterLifeFileRepository extends JpaRepository<AfterLifeFile, Long> {
    List<AfterLifeFile> findByUserIdAndDeliveredFalse(Long userId);



}
