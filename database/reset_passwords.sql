-- ============================================================
-- Reset passwords to plain text after removing Spring Security
-- After this migration, passwords are stored in plain text
-- (no BCrypt hashing anymore)
-- ============================================================

USE FUCarRentingSystem_DB;
GO

-- Set all admin/customers to password "admin" (plain text)
-- User can change password after first login
UPDATE dbo.Account
SET Password = N'admin';
GO

-- After running this, login with:
--   admin / admin
--   customer1 / admin
--   customer2 / admin
--   customer3 / admin

PRINT 'Passwords reset to plain text "admin".';
GO
