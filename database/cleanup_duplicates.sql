-- ============================================================
-- Cleanup script: remove duplicate accounts (keep oldest by ID)
-- Run this ONCE before applying new schema to remove existing dupes
-- ============================================================

USE FUCarRentingSystem_DB;
GO

-- Show duplicates before cleanup
SELECT AccountName, COUNT(*) AS cnt
FROM dbo.Account
GROUP BY AccountName
HAVING COUNT(*) > 1;
GO

-- Delete duplicates: keep only the row with MIN(AccountID) per AccountName
DELETE a
FROM dbo.Account a
INNER JOIN (
    SELECT AccountName, MIN(AccountID) AS keep_id
    FROM dbo.Account
    GROUP BY AccountName
) keeper ON a.AccountName = keeper.AccountName
WHERE a.AccountID > keeper.keep_id;
GO

-- Also clean orphan Customers (whose AccountID no longer exists)
DELETE c
FROM dbo.Customer c
LEFT JOIN dbo.Account a ON c.AccountID = a.AccountID
WHERE a.AccountID IS NULL;
GO

PRINT 'Cleanup complete. Run seed_data.sql if needed.';
GO
