Your API structure
You now have:

POST   /api/accounts
POST   /api/accounts/{accountNumber}/deposit
POST   /api/accounts/{accountNumber}/withdraw
POST   /api/accounts/transfer
GET    /api/accounts/{accountNumber}/balance
GET    /api/accounts/{accountNumber}/transactions
Create account
POST

http://localhost:3030/api/accounts
Body:

{
    "accountNumber": "ACC1001"
}
Response:

{
    "id": 1,
    "accountNumber": "ACC1001",
    "balance": 0.00,
    "createdAt": "2026-08-25T13:30:00"
}
Deposit ₹10,000
POST

http://localhost:3030/api/accounts/ACC1001/deposit
Body:

{
    "amount": 10000
}
Withdraw ₹2,000
POST

http://localhost:3030/api/accounts/ACC1001/withdraw
Body:

{
    "amount": 2000
}
Balance becomes:

10000 - 2000 = 8000
Transfer ₹3,000
First create another account:

{
    "accountNumber": "ACC1002"
}
Then:

POST

http://localhost:3030/api/accounts/transfer
Body:

{
    "fromAccountNumber": "ACC1001",
    "toAccountNumber": "ACC1002",
    "amount": 3000
}
Result:

ACC1001 → ₹5,000
ACC1002 → ₹3,000
And the ledger contains:

ACC1001 → TRANSFER_DEBIT  → ₹3,000
ACC1002 → TRANSFER_CREDIT → ₹3,000


Database Structure:
mysql> use banking_db;
Database changed
mysql> select * from accounts;
+----+----------------+---------+----------------------------+
| id | account_number | balance | created_at                 |
+----+----------------+---------+----------------------------+
|  1 | ACC1001        | 5000.00 | 2026-08-25 14:13:19.352127 |
|  2 | ACC1002        | 3000.00 | 2026-08-25 14:24:13.551429 |
+----+----------------+---------+----------------------------+

2 rows in set (0.01 sec)

mysql> select * from transactions;
+----+----------+------------------------+----------------------------+-----------------+------------+
| id | amount   | description            | timestamp                  | type            | account_id |
+----+----------+------------------------+----------------------------+-----------------+------------+
|  1 | 10000.00 | Amount deposited       | 2026-08-25 14:17:30.762516 | DEPOSIT         |          1 |
|  2 |  2000.00 | Amount withdrawn       | 2026-08-25 14:19:58.983492 | WITHDRAWAL      |          1 |
|  3 |  3000.00 | Transferred to ACC1002 | 2026-08-25 14:28:26.710027 | TRANSFER_DEBIT  |          1 |
|  4 |  3000.00 | Received from ACC1001  | 2026-08-25 14:28:26.711042 | TRANSFER_CREDIT |          2 |
+----+----------+------------------------+----------------------------+-----------------+------------+
4 rows in set (0.01 sec)

mysql> SELECT
    ->     a.account_number,
    ->     a.balance,
    ->     t.type,
    ->     t.amount,
    ->     t.timestamp,
    ->     t.description
    -> FROM accounts a
    -> JOIN transactions t
    ->     ON a.id = t.account_id
    -> WHERE a.account_number = 'ACC1001'
    -> ORDER BY t.timestamp DESC;
+----------------+---------+----------------+----------+----------------------------+------------------------+
| account_number | balance | type           | amount   | timestamp                  | description            |
+----------------+---------+----------------+----------+----------------------------+------------------------+
| ACC1001        | 5000.00 | TRANSFER_DEBIT |  3000.00 | 2026-08-25 14:28:26.710027 | Transferred to ACC1002 |
| ACC1001        | 5000.00 | WITHDRAWAL     |  2000.00 | 2026-08-25 14:19:58.983492 | Amount withdrawn       |
| ACC1001        | 5000.00 | DEPOSIT        | 10000.00 | 2026-08-25 14:17:30.762516 | Amount deposited       |
+----------------+---------+----------------+----------+----------------------------+------------------------+
3 rows in set (0.07 sec)

mysql> DESC accounts;
+----------------+---------------+------+-----+---------+----------------+
| Field          | Type          | Null | Key | Default | Extra          |
+----------------+---------------+------+-----+---------+----------------+
| id             | bigint        | NO   | PRI | NULL    | auto_increment |
| account_number | varchar(255)  | NO   | UNI | NULL    |                |
| balance        | decimal(19,2) | NO   |     | NULL    |                |
| created_at     | datetime(6)   | NO   |     | NULL    |                |
+----------------+---------------+------+-----+---------+----------------+
4 rows in set (0.05 sec)

mysql> desc transaction;
ERROR 1146 (42S02): Table 'banking_db.transaction' doesn't exist
mysql> desc transactions;
+-------------+-----------------------------------------------------------------+------+-----+---------+----------------+
| Field       | Type                                                            | Null | Key | Default | Extra          |
+-------------+-----------------------------------------------------------------+------+-----+---------+----------------+
| id          | bigint                                                          | NO   | PRI | NULL    | auto_increment |
| amount      | decimal(19,2)                                                   | NO   |     | NULL    |                |
| description | varchar(255)                                                    | YES  |     | NULL    |                |
| timestamp   | datetime(6)                                                     | NO   |     | NULL    |                |
| type        | enum('DEPOSIT','TRANSFER_CREDIT','TRANSFER_DEBIT','WITHDRAWAL') | NO   |     | NULL    |                |
| account_id  | bigint                                                          | NO   | MUL | NULL    |                |
+-------------+-----------------------------------------------------------------+------+-----+---------+----------------+
6 rows in set (0.01 sec)

mysql>
