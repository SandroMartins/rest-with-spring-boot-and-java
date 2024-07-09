package br.com.erudio.unittests.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;
import br.com.erudio.services.BookServices;
import br.com.erudio.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {
	
	MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() throws Exception {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
		
		var result = service.findById(entity.getId());
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		System.out.println(result.toString());
		
		assertTrue(result.toString().contains("links: [</api/books/v1/1>;rel=\"self\"]"));
		
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		//assertEquals(result.getPrice());
		//assertEquals(result.getLaunchDate());
	}

	@Test
	void testFindAll() {
		fail("Not yet implemented");
	}


	@Test
	void testCreate() throws Exception {
		Book entity = input.mockEntity(1);
		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.save(entity)).thenReturn(persisted);
		var result = service.create(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/books/v1/1>;rel=\"self\"]"));
		
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		//assertEquals(result.getPrice());
		//assertEquals(result.getLaunchDate());
	}

	@Test
	void testUpdate() throws Exception {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		var result = service.update(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/books/v1/1>;rel=\"self\"]"));
		
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		//assertEquals(result.getPrice());
		//assertEquals(result.getLaunchDate());
	}

	@Test
	void testDelete() throws Exception {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
	
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
		
		service.delete(entity.getId());		
	}
}
