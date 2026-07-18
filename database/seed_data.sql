-- ============================================================
-- FUCarRentingSystem_DB - Demo seed data
-- Run this AFTER FUCarRentingSystem_DB.sql (which creates tables)
-- Idempotent: only inserts if data not present
--
-- IMPORTANT: All accounts below use the same BCrypt hash which
-- corresponds to password "admin" for ALL accounts (admin, customer1,
-- customer2, customer3). This is because BCrypt salts are random.
--
-- For per-account unique passwords, run the Spring Boot app once
-- and let DataSeeder.java (config/DataSeeder.java) auto-generate
-- proper hashed passwords for customer1/2/3.
-- ============================================================

USE FUCarRentingSystem_DB;
GO

-- ============================================================
-- Producers
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'Toyota')
BEGIN
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country)
    VALUES (N'Toyota', N'1 Toyota City, Aichi', N'Japan');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'Honda')
BEGIN
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country)
    VALUES (N'Honda', N'2 Honda Way, Tokyo', N'Japan');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'BMW')
BEGIN
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country)
    VALUES (N'BMW', N'Petuelring 130, Munich', N'Germany');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'Ford')
BEGIN
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country)
    VALUES (N'Ford', N'1 American Rd, Dearborn, MI', N'USA');
END
GO

-- ============================================================
-- Accounts (BCrypt hashed passwords)
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'admin')
BEGIN
    INSERT INTO dbo.Account (AccountName, Email, Password, Role)
    VALUES (N'admin', N'admin', N'$2a$10$p9nZqLJZK4tLGjdqMKOM4.iDUuch92yPU36gITXJfYqwlRaCuDhLG', N'ADMIN');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'customer1')
BEGIN
    INSERT INTO dbo.Account (AccountName, Email, Password, Role)
    VALUES (N'customer1', N'customer1@fucar.com', N'$2a$10$p9nZqLJZK4tLGjdqMKOM4.iDUuch92yPU36gITXJfYqwlRaCuDhLG', N'CUSTOMER');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'customer2')
BEGIN
    INSERT INTO dbo.Account (AccountName, Email, Password, Role)
    VALUES (N'customer2', N'customer2@fucar.com', N'$2a$10$p9nZqLJZK4tLGjdqMKOM4.iDUuch92yPU36gITXJfYqwlRaCuDhLG', N'CUSTOMER');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'customer3')
BEGIN
    INSERT INTO dbo.Account (AccountName, Email, Password, Role)
    VALUES (N'customer3', N'customer3@fucar.com', N'$2a$10$p9nZqLJZK4tLGjdqMKOM4.iDUuch92yPU36gITXJfYqwlRaCuDhLG', N'CUSTOMER');
END
GO

-- ============================================================
-- Customers
-- ============================================================
DECLARE @adminAcc  INT = (SELECT AccountID FROM dbo.Account WHERE Email = 'admin');
DECLARE @c1 INT = (SELECT AccountID FROM dbo.Account WHERE Email = 'customer1@fucar.com');
DECLARE @c2 INT = (SELECT AccountID FROM dbo.Account WHERE Email = 'customer2@fucar.com');
DECLARE @c3 INT = (SELECT AccountID FROM dbo.Account WHERE Email = 'customer3@fucar.com');

IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE IdentityCard = '000000000')
BEGIN
    INSERT INTO dbo.Customer (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
    VALUES (N'System Administrator', '0900000000', '1990-01-01', '000000000', N'ADMIN-LIC', '2015-01-01', @adminAcc);
END
IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE IdentityCard = '079095111111')
BEGIN
    INSERT INTO dbo.Customer (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
    VALUES (N'Nguyen Van A', '0901111111', '1995-05-12', '079095111111', N'B2-12345678', '2018-08-20', @c1);
END
IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE IdentityCard = '079092222222')
BEGIN
    INSERT INTO dbo.Customer (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
    VALUES (N'Tran Thi B', '0902222222', '1992-11-03', '079092222222', N'B2-23456789', '2016-03-15', @c2);
END
IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE IdentityCard = '079088333333')
BEGIN
    INSERT INTO dbo.Customer (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
    VALUES (N'Le Van C', '0903333333', '1988-07-25', '079088333333', N'B2-34567890', '2014-12-01', @c3);
END
GO

-- ============================================================
-- Cars (ProducerID via subqueries)
-- ============================================================
DECLARE @toyota INT = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Toyota');
DECLARE @honda  INT = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Honda');
DECLARE @bmw    INT = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'BMW');
DECLARE @ford   INT = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Ford');

IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Toyota Camry 2.5Q')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Toyota Camry 2.5Q', 2023, N'White', 5, N'Sedan hang D, may 2.5L hybrid, tiet kiem nhien lieu, noi that da.', '2024-01-15', @toyota, 1500000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Toyota Corolla Altis')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Toyota Corolla Altis', 2022, N'Silver', 5, N'Sedan hang C, ben bi, phu hop gia dinh.', '2023-06-10', @toyota, 1100000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Toyota Vios 1.5G')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Toyota Vios 1.5G', 2022, N'White', 5, N'Sedan hang B, tiet kiem, phu hop do thi.', '2023-03-20', @toyota, 800000, N'Unavailable');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Honda Civic RS')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Honda Civic RS', 2023, N'Black', 5, N'Sedan the thao, dong co 1.5L turbo, thiet ke tre trung.', '2024-02-05', @honda, 1400000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Honda CR-V L')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Honda CR-V L', 2024, N'White', 7, N'SUV 7 cho, rong rai, phu hop gia dinh lon.', '2024-04-12', @honda, 1800000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Honda City RS')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Honda City RS', 2023, N'Silver', 5, N'Sedan hang B, thiet ke the thao.', '2023-09-01', @honda, 900000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'BMW X5 xDrive40i')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'BMW X5 xDrive40i', 2024, N'Black', 5, N'SUV hang sang, dong co 3.0L turbo, noi that da Nappa.', '2024-03-20', @bmw, 4500000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'BMW 320i Sport Line')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'BMW 320i Sport Line', 2023, N'Blue', 5, N'Sedan the thao, dong co 2.0L turbo 184hp.', '2023-11-05', @bmw, 3200000, N'Rented');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Ford Ranger Wildtrak')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Ford Ranger Wildtrak', 2024, N'Red', 5, N'Ban tai the thao, dong co 2.0L bi-turbo, off-road tot.', '2024-05-08', @ford, 2200000, N'Available');
END
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Ford Everest Titanium')
BEGIN
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    VALUES (N'Ford Everest Titanium', 2023, N'White', 7, N'SUV 7 cho, dong co 2.0L bi-turbo, nhieu option cao cap.', '2023-12-15', @ford, 2500000, N'Rented');
END
GO

-- ============================================================
-- Rentals (mix of Active/Completed/Cancelled over past 30 days)
-- ============================================================
DECLARE @cust1 INT = (SELECT CustomerID FROM dbo.Customer WHERE IdentityCard = '079095111111');
DECLARE @cust2 INT = (SELECT CustomerID FROM dbo.Customer WHERE IdentityCard = '079092222222');
DECLARE @cust3 INT = (SELECT CustomerID FROM dbo.Customer WHERE IdentityCard = '079088333333');

DECLARE @car1  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Camry 2.5Q');
DECLARE @car2  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Corolla Altis');
DECLARE @car3  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Vios 1.5G');
DECLARE @car4  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Honda Civic RS');
DECLARE @car5  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Honda CR-V L');
DECLARE @car6  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Honda City RS');
DECLARE @car7  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'BMW X5 xDrive40i');
DECLARE @car8  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'BMW 320i Sport Line');
DECLARE @car9  INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Ford Ranger Wildtrak');
DECLARE @car10 INT = (SELECT CarID FROM dbo.Car WHERE CarName = 'Ford Everest Titanium');

IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust1 AND CarID = @car1 AND PickupDate = DATEADD(DAY, -5, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust1, @car1, DATEADD(DAY, -5, CAST(GETDATE() AS DATE)), DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 10500000, N'Active');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust2 AND CarID = @car4 AND PickupDate = DATEADD(DAY, -3, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust2, @car4, DATEADD(DAY, -3, CAST(GETDATE() AS DATE)), DATEADD(DAY, 4, CAST(GETDATE() AS DATE)), 9800000, N'Active');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust1 AND CarID = @car2 AND PickupDate = DATEADD(DAY, -15, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust1, @car2, DATEADD(DAY, -15, CAST(GETDATE() AS DATE)), DATEADD(DAY, -10, CAST(GETDATE() AS DATE)), 5500000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust2 AND CarID = @car5 AND PickupDate = DATEADD(DAY, -20, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust2, @car5, DATEADD(DAY, -20, CAST(GETDATE() AS DATE)), DATEADD(DAY, -13, CAST(GETDATE() AS DATE)), 12600000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust3 AND CarID = @car7 AND PickupDate = DATEADD(DAY, -25, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust3, @car7, DATEADD(DAY, -25, CAST(GETDATE() AS DATE)), DATEADD(DAY, -18, CAST(GETDATE() AS DATE)), 31500000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust1 AND CarID = @car8 AND PickupDate = DATEADD(DAY, -28, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust1, @car8, DATEADD(DAY, -28, CAST(GETDATE() AS DATE)), DATEADD(DAY, -22, CAST(GETDATE() AS DATE)), 19200000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust2 AND CarID = @car6 AND PickupDate = DATEADD(DAY, -10, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust2, @car6, DATEADD(DAY, -10, CAST(GETDATE() AS DATE)), DATEADD(DAY, -5, CAST(GETDATE() AS DATE)), 4500000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust3 AND CarID = @car1 AND PickupDate = DATEADD(DAY, -12, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust3, @car1, DATEADD(DAY, -12, CAST(GETDATE() AS DATE)), DATEADD(DAY, -8, CAST(GETDATE() AS DATE)), 6000000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust1 AND CarID = @car3 AND PickupDate = DATEADD(DAY, -2, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust1, @car3, DATEADD(DAY, -2, CAST(GETDATE() AS DATE)), DATEADD(DAY, 5, CAST(GETDATE() AS DATE)), 5600000, N'Active');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust2 AND CarID = @car9 AND PickupDate = DATEADD(DAY, -7, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust2, @car9, DATEADD(DAY, -7, CAST(GETDATE() AS DATE)), DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 13200000, N'Completed');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust3 AND CarID = @car4 AND PickupDate = DATEADD(DAY, -18, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust3, @car4, DATEADD(DAY, -18, CAST(GETDATE() AS DATE)), DATEADD(DAY, -14, CAST(GETDATE() AS DATE)), 5600000, N'Cancelled');
END
IF NOT EXISTS (SELECT 1 FROM dbo.CarRental WHERE CustomerID = @cust1 AND CarID = @car6 AND PickupDate = DATEADD(DAY, -22, CAST(GETDATE() AS DATE)))
BEGIN
    INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
    VALUES (@cust1, @car6, DATEADD(DAY, -22, CAST(GETDATE() AS DATE)), DATEADD(DAY, -16, CAST(GETDATE() AS DATE)), 5400000, N'Completed');
END
GO

-- ============================================================
-- Reviews (only for Completed rentals)
-- ============================================================
DECLARE @r3 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 0 ROWS FETCH NEXT 1 ROW ONLY);
DECLARE @r4 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 1 ROWS FETCH NEXT 1 ROW ONLY);
DECLARE @r5 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 2 ROWS FETCH NEXT 1 ROW ONLY);
DECLARE @r6 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 3 ROWS FETCH NEXT 1 ROW ONLY);
DECLARE @r7 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 4 ROWS FETCH NEXT 1 ROW ONLY);
DECLARE @r8 INT = (SELECT TOP 1 CarRenID FROM dbo.CarRental WHERE Status = 'Completed' ORDER BY CarRenID OFFSET 5 ROWS FETCH NEXT 1 ROW ONLY);

IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r3)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r3, 5, N'Xe sach, chay em, nhan vien nhiet tinh. Se thue lai!');
IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r4)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r4, 4, N'CR-V rong rai, phu hop di du lich gia dinh.');
IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r5)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r5, 5, N'BMW X5 tuyet voi, dang dong tien.');
IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r6)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r6, 4, N'320i van hanh muot ma, noi that dep.');
IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r7)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r7, 3, N'Binh thuong, co chut tre gio tra xe.');
IF NOT EXISTS (SELECT 1 FROM dbo.Review WHERE CarRenID = @r8)
    INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES (@r8, 5, N'Camry hybrid rat tiet kiem xang.');
GO

PRINT 'Seed data inserted successfully.';
PRINT 'Accounts: admin/admin | customer1/customer1 | customer2/customer2 | customer3/customer3';
GO