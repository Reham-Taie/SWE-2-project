package com.FCI.SWE.ServicesModels;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.Models.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
 * <h1>User Entity class</h1>
 * <p>
 * This class will act as a model for user, it will holds user data
 * </p>
 *
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 */
public class UserEntity {
	private String name;
	private String email;
	private String password;
	private long id;

	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */
	public UserEntity(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
	
	private void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}

	
	/**
	 * 
	 * This static method will form UserEntity class using user name and
	 * password This method will serach for user in datastore
	 * 
	 * @param name
	 *            user name
	 * @param pass
	 *            user password
	 * @return Constructed user entity
	 */

	public static UserEntity getUser(String name, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if (entity.getProperty("name").toString().equals(name)
					&& entity.getProperty("password").toString().equals(pass)) {
				UserEntity returnedUser = new UserEntity(entity.getProperty(
						"name").toString(), entity.getProperty("email")
						.toString(), entity.getProperty("password").toString());
				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}
	public static Vector<UserEntity> searchUser(String sname) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		Vector<UserEntity>returnedUsers=new Vector<UserEntity>();
		for (Entity entity : pq.asIterable()) {
			String currentName=entity.getProperty("name").toString() ;
			if (currentName.contains(sname))
			{
				UserEntity user=new UserEntity(entity.getProperty("name").toString() , 
						entity.getProperty("email").toString(),
						entity.getProperty("password").toString());
				user.setId(entity.getKey().getId());
				returnedUsers.add(user);
			}
		}
	
		return returnedUsers;
	}

	/**
	 * This method will be used to save user object in datastore
	 * 
	 * @return boolean if user is saved correctly or not
	 */
	public Boolean saveUser() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		System.out.println("Size = " + list.size());
		
		try {
		Entity employee = new Entity("users", list.size() + 2);

		employee.setProperty("name", this.name);
		employee.setProperty("email", this.email);
		employee.setProperty("password", this.password);
		
		datastore.put(employee);
		txn.commit();
		}
		finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}
	
	
	public static void friend(String friendEmail) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query gaeQuery = new Query("friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		
		Entity employee = new Entity("friends", list.size() + 2);
		employee.setProperty("friendEmail",friendEmail );
		
		employee.setProperty("myEmail", User.getCurrentActiveUser().getEmail().toString());
		employee.setProperty("status", "false");
		
		
		datastore.put(employee);
		
		
		//return true;
	}
	
	
	public static UserEntity getrequest(String semail) {
		String myEmail=User.getCurrentActiveUser().getEmail().toString();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		System.out.println("semail = " + semail);
		System.out.println("myemail " + myEmail);
		
		Query gaeQuery = new Query("friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) {
			if (entity.getProperty("friendEmail").toString().equals(myEmail) && entity.getProperty("myEmail").toString().equals(semail) ) {
				
				entity.setProperty("status", "accept");
						
				datastore.put(entity);
				
			}
			System.out.println("friendemail" + entity.getProperty("friendEmail").toString());
		}

		return null;
	}

}