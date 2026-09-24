package com.lms.Backend.admin.repository;

import com.lms.Backend.admin.entity.PlatformSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformSettingRepository extends JpaRepository<PlatformSetting, Long> {

    Optional<PlatformSetting> findBySettingKey(String settingKey);

    List<PlatformSetting> findByCategory(String category);

    List<PlatformSetting> findAllByOrderByCategoryAscSettingKeyAsc();
}
