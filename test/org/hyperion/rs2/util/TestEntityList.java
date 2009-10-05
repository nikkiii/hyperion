package org.hyperion.rs2.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.util.EntityList;
import org.junit.Before;
import org.junit.Test;

public class TestEntityList {
	
	private static class EntityStub extends Entity {

		@Override
		public void addToRegion(Region region) {
			
		}

		@Override
		public void removeFromRegion(Region region) {
			
		}

		@Override
		public int getClientIndex() {
			return 0;
		}

		@Override
		public void inflictDamage(int damage, HitType type) {
			
		}
		
	}
	
	private EntityList<EntityStub> list;

	@Before
	public void setUp() throws Exception {
		list = new EntityList<EntityStub>(10);
	}

	@Test
	public void testAdd() {
		list.add(new EntityStub());
		assertEquals(1, list.size());
	}

	@SuppressWarnings("serial")
	@Test
	public void testAddAll() {
		List<EntityStub> stubs = new ArrayList<EntityStub>() {{
			add(new EntityStub());
			add(new EntityStub());
			add(new EntityStub());
		}};
		list.addAll(stubs);
		assertEquals(3, list.size());
	}

	@Test
	public void testClear() {
		list.add(new EntityStub());
		list.clear();
		assertEquals(0, list.size());
	}

	@Test
	public void testContains() {
		EntityStub stub = new EntityStub();
		assertFalse(list.contains(stub));
		list.add(stub);
		assertTrue(list.contains(stub));
		list.remove(stub);
		assertFalse(list.contains(stub));
	}

	@SuppressWarnings("serial")
	@Test
	public void testContainsAll() {
		List<EntityStub> stubs = new ArrayList<EntityStub>() {{
			add(new EntityStub());
			add(new EntityStub());
			add(new EntityStub());
		}};
		list.addAll(stubs);
		assertTrue(list.containsAll(stubs));
	}
	
	@Test
	public void testGet() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		assertEquals(stub1, list.get(1));
		assertEquals(stub2, list.get(2));
	}
	
	@Test
	public void testIndexOf() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		assertEquals(1, list.indexOf(stub1));
		assertEquals(2, list.indexOf(stub2));
	}

	@Test
	public void testIsEmpty() {
		assertTrue(list.isEmpty());
		list.add(new EntityStub());
		assertFalse(list.isEmpty());
	}

	@Test
	public void testIterator() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		Iterator<EntityStub> it$ = list.iterator();
		assertTrue(it$.hasNext());
		assertEquals(stub1, it$.next());
		assertTrue(it$.hasNext());
		assertEquals(stub2, it$.next());
		assertFalse(it$.hasNext());
		it$.remove();
		assertEquals(1, list.size());
		assertTrue(list.contains(stub1));
		assertFalse(list.contains(stub2));
	}

	@Test
	public void testRemove() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		list.remove(stub1);
		assertEquals(1, list.size());
		assertFalse(list.contains(stub1));
		assertTrue(list.contains(stub2));
		list.remove(stub2);
		assertFalse(list.contains(stub2));
		assertTrue(list.isEmpty());
	}

	@SuppressWarnings("serial")
	@Test
	public void testRemoveAll() {
		List<EntityStub> stubs = new ArrayList<EntityStub>() {{
			add(new EntityStub());
			add(new EntityStub());
			add(new EntityStub());
		}};
		list.addAll(stubs);
		assertEquals(3, list.size());
		list.removeAll(stubs);
		assertEquals(0, list.size()); 
	}

	@SuppressWarnings("serial")
	@Test
	public void testRetainAll() {
		final EntityStub stub1 = new EntityStub();
		final EntityStub stub2 = new EntityStub();
		final EntityStub stub3 = new EntityStub();
		final EntityStub stub4 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		list.add(stub3);
		list.add(stub4);
		List<EntityStub> stubs = new ArrayList<EntityStub>() {{
			add(stub2);
			add(stub4);
		}};
		list.retainAll(stubs);
		assertEquals(2, list.size());
		assertTrue(list.contains(stub2));
		assertTrue(list.contains(stub4));
		assertFalse(list.contains(stub1));
		assertFalse(list.contains(stub3));
	}

	@Test
	public void testSize() {
		assertEquals(0, list.size());
		list.add(new EntityStub());
		assertEquals(1, list.size());
		list.add(new EntityStub());
		list.add(new EntityStub());
		list.add(new EntityStub());
		assertEquals(4, list.size());
		list.clear();
		assertEquals(0, list.size());
	}

	@Test
	public void testToArray() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		Entity[] expected = new Entity[] { stub1, stub2 };
		Entity[] actual = list.toArray();
		assertArrayEquals(actual, expected);
	}

	@Test
	public void testToArrayTArray() {
		EntityStub stub1 = new EntityStub();
		EntityStub stub2 = new EntityStub();
		list.add(stub1);
		list.add(stub2);
		EntityStub[] expected = new EntityStub[] { stub1, stub2 };
		EntityStub[] actual = list.toArray(new EntityStub[0]);
		assertArrayEquals(actual, expected);
	}

}
