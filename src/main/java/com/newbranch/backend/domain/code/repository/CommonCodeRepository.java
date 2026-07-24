package com.newbranch.backend.domain.code.repository;

import com.newbranch.backend.domain.code.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
}
