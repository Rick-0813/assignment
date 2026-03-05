public class DVD extends LibraryItem {

    private String director ;
    private int duration ;
    private int stockQuantity ;

    //Constructor
    public DVD (String itemId , String title , String director , int duration , int stockQuantity) {
        super(itemId , title);
        this.director = director ;
        this.duration = duration ;
        this.stockQuantity   = stockQuantity;

        if (this.stockQuantity > 0) {
            setAvailable(true);
        }
        else{
            setAvailable(false);
        }
    }


    //Getter
    public String getDirector() {
        return director;
    }

    public int getDuration() {
        return duration ;
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
        System.out.println("-----DVD Details-----");
        System.out.println("DVD ID : " + getItemId());
        System.out.println("Title : " + getTitle());
        System.out.println("Director : " + getDirector());
        System.out.println("Duration : " + getDuration());
        System.out.println("Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("Status : Available");
        }
        else {
            System.out.println("Status : Out of Stock");
        }
        System.out.println("---------------------");
    }

}
