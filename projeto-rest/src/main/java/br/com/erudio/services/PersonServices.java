package br.com.erudio.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.erudio.controllers.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;

@Service
public class PersonServices {
	
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	PersonRepository repository;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;
	
	@Autowired
	PersonMapper mapper;

	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {

		logger.info("Finding all people!");
		
		var personPage = repository.findAll(pageable);
		var personVosPage = personPage.map(p-> DozerMapper.parseObject(p, PersonVO.class));
		
		personVosPage.map(
				p -> {
					try {
						return p.add(
								linkTo(methodOn(PersonController.class)
										.findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				});

		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personVosPage, link);
	}
	
	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable) {
		
		logger.info("Finding person by name!");
		
		var personPage = repository.findPersonsByName(firstName, pageable);
		var personVosPage = personPage.map(p-> DozerMapper.parseObject(p, PersonVO.class));
		
		personVosPage.map(
				p -> {
					try {
						return p.add(
								linkTo(methodOn(PersonController.class)
										.findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				});
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO findById(Long id) throws Exception {
		
		logger.info("Finding one person!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
	}
	
	public PersonVO create(PersonVO person) throws Exception {

		logger.info("Creating one person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo =  DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
//	public PersonVOV2 createV2(PersonVOV2 person) {
//
//		logger.info("Creating one person!");
//		var entity = mapper.convertVoToEntity(person);
//		var vo =  mapper.convertEntityToVo(repository.save(entity));
//		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
//		
//		return vo;
//	}
	
	public PersonVO update(PersonVO person) throws Exception {
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	@Transactional
	public PersonVO disablePerson(Long id) throws Exception {
		
		logger.info("Disabling one person!");
		
		 repository.disablePerson(id);
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one person!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
