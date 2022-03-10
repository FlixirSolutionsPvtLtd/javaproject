package com.alinote.api.repository.impl;

import com.alinote.api.model.*;
import com.alinote.api.repository.*;
import com.mongodb.client.result.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
public class CustomItemRepositoryImpl implements CustomItemRepository {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public void updateItemQuantity(String name, float newQuantity) {
		Query query = new Query(Criteria.where("name").is(name));
		Update update = new Update();
		update.set("quantity", newQuantity);
		
		UpdateResult result = mongoTemplate.updateFirst(query, update, GroceryItem.class);
		
		if(result == null)
			log.debug("No documents updated");
		else
			log.debug(result.getModifiedCount() + " document(s) updated..");

	}

}
