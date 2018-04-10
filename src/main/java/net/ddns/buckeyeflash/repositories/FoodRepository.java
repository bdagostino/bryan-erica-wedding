package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FoodRepository extends PagingAndSortingRepository<Food, Integer> {

    Page<Food> findByTypeStartingWith(String type, Pageable pageable);

    List<Food> findAll();
}
