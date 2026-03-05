public class Magazine extends LibraryItem {

    private String publisher ;
    private int issueNumber ;
    private int stockQuantity ;

    //Constructor
    public Magazine (String itemId , String title , String publisher , int issueNumber , int stockQuantity) {
        super(itemId , title);
        this.publisher = publisher ;
        this.issueNumber = issueNumber ;
        this.stockQuantity   = stockQuantity;

        if (this.stockQuantity > 0) {
            setAvailable(true);
        }
        else{
            setAvailable(false);
        }
    }


    //Getter
    public String getPublisher() {
        return publisher;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }


    //Setter
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        setAvailable(this.stockQuantity > 0 );
    }


    @Override
    public void displayItemDetails() {
        System.out.println("-----Magazine Details-----");
        System.out.println("Magazine ID : " + getItemId());
        System.out.println("Title : " + getTitle());
        System.out.println("Publisher : " + publisher);
        System.out.println("Issue Number : " + issueNumber);
        System.out.println("Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("Status : Available");
        }
        else {
            System.out.println("Status : Out of Stock");
        }
        System.out.println("--------------------------");
    }

}
