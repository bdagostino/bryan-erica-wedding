package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FoodRepository extends PagingAndSortingRepository<Food, Long> {

    Page<Food> findByTypeStartingWith(String type, Pageable pageable);

    Food findById(Integer id);
}
