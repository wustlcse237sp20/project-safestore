package tables;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Addresses")
public class AddressEntity extends BaseDaoEnabled<AddressEntity, Integer> {
	
	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private int id;
	
	@DatabaseField(columnName = "street_address", canBeNull = false)
	private String streetAddress;
	
	@DatabaseField(columnName = "city", canBeNull = false)
	private String city;
	
	@DatabaseField(columnName = "state", canBeNull = false)
	private String state;
	
	@DatabaseField(columnName = "zip_code", canBeNull = false)
	private String zipCode;
	
	// these two are used to store the accounts and cards for the address
	// 	they are NOT database columns, they are just used to store 
	//	the objects to make life easier
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<CreditCardEntity> creditCards;
	
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<DebitCardEntity> debitCards;
	
	// ORMLite needs a no-argument constructor
	public AddressEntity() {}
	
	public AddressEntity(String streetAddress, String city, String state, String zipCode) {
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}
	
	// no setter for id because it is auto-generated and shouldn't be changed once made
	public int getId() {
		return id;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}

	// only getters for ForeignCollections because the queries will automatically set them
	public ForeignCollection<CreditCardEntity> getCreditCards() {
		return creditCards;
	}

	public ForeignCollection<DebitCardEntity> getDebitCards() {
		return debitCards;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setState(String state) {
		this.state = state;
	} 
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
