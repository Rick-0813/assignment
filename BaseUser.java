public class BaseUser {
    private String name;
    private String userID;
    private String userType;
    private String email;

    public BaseUser(String name, String userID, String userType, String email) {
        this.name = name;
        this.userID = userID;
        this.userType = userType;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUserID() { return userID; }
    
    public String getUserType() { return userType; }
    public void setUserType(String type) { this.userType = type; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseUser user = (BaseUser) obj;
        return userID.equals(user.userID);
    }
}