# Card Validation from MySQL Collection

This collection validates that all card records in the MySQL database exist in the KiGa 3000 REST API and have matching data.

## Overview

The collection connects to the MySQL database, retrieves all card records, and validates each one against the REST API endpoint `/api/cards/{id}`.

## Data Source

**Name:** Cards MySQL Database  
**Location:** `postman/datasets/Cards-MySQL-Database/`

### Connection Details
- **Host:** localhost:3306
- **Database:** Kindergarten
- **Table:** Karteikarte
- **Username:** KiGa
- **Password:** Kiga3000

### Query
```sql
SELECT id, kindvorname as vorname, kindnachname as nachname, gruppe 
FROM Karteikarte 
ORDER BY id
```

## How to Run

1. **Ensure the API is running:**
   ```bash
   ./run.sh
   ```
   The API should be accessible at `http://127.0.0.1:8080`

2. **Ensure MySQL is running** with the Kindergarten database populated

3. **Open the Collection Runner:**
   - Click on the "Card Validation from MySQL" collection
   - Click the "Run" button

4. **Select the Data Source:**
   - In the Collection Runner, select "Cards MySQL Database" as the data source
   - The runner will iterate through all 17+ card records

5. **Run the Collection:**
   - Click "Run Card Validation from MySQL"
   - Watch as each card is validated

## What Gets Validated

For each card record from the database, the collection validates:

✅ **Status Code:** Response returns 200 OK  
✅ **Card ID:** Matches the database ID  
✅ **First Name (vorname):** Matches the database value  
✅ **Last Name (nachname):** Matches the database value  
✅ **Group (gruppe):** Matches the database group number  

## Expected Results

- **Total Iterations:** Equal to the number of cards in the database (17+ cards)
- **All Tests Pass:** If the API and database are in sync
- **Failed Tests:** Indicate discrepancies between the database and API

## Troubleshooting

### Connection Failed
- Verify MySQL is running: `mysql -u KiGa -pKiga3000 Kindergarten`
- Check the database credentials in the data source configuration

### API Not Responding
- Ensure the KiGa 3000 API is running: `./run.sh`
- Verify the API is accessible: `curl http://127.0.0.1:8080/api/health`

### Test Failures
- Check if the card exists in both the database and API
- Verify the data matches between database and API
- Review the console logs for detailed validation messages

## Files

- **Collection Definition:** `.resources/definition.yaml`
- **Validate Card Request:** `Validate Card.request.yaml`
- **Data Source:** `../../datasets/Cards-MySQL-Database/Cards-MySQL-Database.dataset.yaml`
