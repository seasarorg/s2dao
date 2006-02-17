/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dao.impl;

import java.sql.Timestamp;

/**
 * @author higa
 *
 */
public class DefaultBeanMetaDataImplTest extends BeanMetaDataImplTest {
	/**
	 * Constructor for InvocationImplTest.
	 * @param arg0
	 */
	public DefaultBeanMetaDataImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DefaultBeanMetaDataImplTest.class);
	}
	
	public void setUp() {
		include("DefaultBeanMetaDataTest.dicon");
	}
	protected Class getBeanClass(String className){
		if(className.equals("MyBean")){
			return MyBean.class;
		}else if(className.equals("Employee")){
			return Employee.class;
		}else if(className.equals("Department")){
			return Department.class;
		}else if(className.equals("Employee4")){
			return Employee4.class;
		}else if(className.equals("Ddd")){
			return Ddd.class;
		}else if(className.equals("Eee")){
			return Eee.class;
		}else if(className.equals("Fff")){
			return Fff.class;
		}else if(className.equals("IdentityTable")){
			return IdentityTable.class;
		}
		return null;
	}
	
	public static class MyBean {
		public static final String TABLE = "MyBean";
		public static final String aaa_ID = "assigned";
		public static final String bbb_COLUMN = "myBbb";
		public static final int ccc_RELNO = 0;
		public static final String ccc_RELKEYS = "ddd:id";
		
		private Integer aaa_;
		private String bbb_;
		private Ccc ccc_;
		private Integer ddd_;
		
		public Integer getAaa() {
			return aaa_;
		}
		
		public void setAaa(Integer aaa) {
			aaa_ = aaa;
		}
		
		public String getBbb() {
			return bbb_;
		}
		
		public void setBbb(String bbb) {
			bbb_ = bbb;
		}
		
		public Ccc getCcc() {
			return ccc_;
		}
		
		public void setCcc(Ccc ccc) {
			ccc_ = ccc;
		}
		
		public Integer getDdd() {
			return ddd_;
		}
		
		public void setDdd(Integer ddd) {
			ddd_ = ddd;
		}
	}
	
	public static class Ccc {
		public static final String id_ID = "assigned";
		private Integer id_;
		public Integer getId() {
			return id_;
		}
		public void setId(Integer id) {
			id_ = id;
		}
	}
	
	public static class Ddd extends Ccc {
		public static final String NO_PERSISTENT_PROPS = "";
		private String name_;
		public String getName() {
			return name_;
		}
		public void setName(String name) {
			name_ = name;
		}
	}
	
	public static class Eee extends Ccc {
		public static final String NO_PERSISTENT_PROPS = "name";
		private String name_;
		public String getName() {
			return name_;
		}
		public void setName(String name) {
			name_ = name;
		}
	}
	
	public static class Fff {
		public static final String VERSION_NO_PROPERTY = "version";
		public static final String TIMESTAMP_PROPERTY = "updated";
		private int version_;
		private Integer id_;
		private Timestamp updated_;
		public Integer getId() {
			return id_;
		}
		public void setId(Integer id) {
			id_ = id;
		}
		public int getVersion() {
			return version_;
		}
		public void setVersion(int version) {
			version_ = version;
		}
		public Timestamp getUpdated() {
			return updated_;
		}
		public void setUpdated(Timestamp updated) {
			updated_ = updated;
		}
	}
}