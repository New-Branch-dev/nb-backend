package com.newbranch.backend.domain.code.repository;

import com.newbranch.backend.domain.code.entity.CommonCode;
import com.newbranch.backend.domain.code.entity.CommonCodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeValueRepository extends JpaRepository<CommonCodeValue, Long> {

    List<CommonCodeValue> findAllByCommonCodeAndIsActiveTrue(CommonCode commonCode);
}
