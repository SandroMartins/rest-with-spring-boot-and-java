package br.com.erudio.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;
	
	@Autowired
	PagedResourcesAssembler<BookVO> assembler;
	
	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {

		logger.info("Finding all people!");
		
		var bookPage = repository.findAll(pageable);
		var bookVosPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
		
		bookVosPage.map(
				p -> {
					try {
						return p.add(
								linkTo(methodOn(BookController.class)
										.findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				});

		Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(bookVosPage, link);
	}

	public BookVO findById(Long id) throws Exception {
		
		logger.info("Finding one Book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		
		return vo;
	}
	
	public BookVO create(BookVO Book) throws Exception {

		logger.info("Creating one Book!");
		var entity = DozerMapper.parseObject(Book, Book.class);
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public BookVO update(BookVO Book) throws Exception {
		
		logger.info("Updating one Book!");
		
		var entity = repository.findById(Book.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setAuthor(Book.getAuthor());
		entity.setPrice(Book.getPrice());
		entity.setTitle(Book.getTitle());
		entity.setLaunchDate(Book.getLaunchDate());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one Book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
