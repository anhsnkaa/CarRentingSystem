-- ============================================================
-- FUCarRentingSystem_DB - Database creation script
-- Assignment 01 v3.0 spec
-- ============================================================

USE master;
GO

IF DB_ID('FUCarRentingSystem_DB') IS NOT NULL
BEGIN
    ALTER DATABASE FUCarRentingSystem_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE FUCarRentingSystem_DB;
END
GO

CREATE DATABASE FUCarRentingSystem_DB;
GO

USE FUCarRentingSystem_DB;
GO

-- ============================================================
-- Table: CarProducer
-- ============================================================
IF OBJECT_ID('dbo.CarProducer', 'U') IS NOT NULL DROP TABLE dbo.CarProducer;
CREATE TABLE dbo.CarProducer (
    ProducerID    INT            IDENTITY(1,1) NOT NULL,
    ProducerName  NVARCHAR(100)  NOT NULL,
    Address       NVARCHAR(200)  NOT NULL,
    Country       NVARCHAR(100)  NOT NULL,
    CONSTRAINT PK_CarProducer PRIMARY KEY (ProducerID)
);
GO

-- ============================================================
-- Table: Account
-- ============================================================
IF OBJECT_ID('dbo.Account', 'U') IS NOT NULL DROP TABLE dbo.Account;
CREATE TABLE dbo.Account (
    AccountID    INT           IDENTITY(1,1) NOT NULL,
    AccountName  NVARCHAR(100) NOT NULL,
    Email        VARCHAR(200)  NOT NULL,
    Password     VARCHAR(200)  NOT NULL,
    Role         NVARCHAR(10)  NOT NULL,
    CONSTRAINT PK_Account PRIMARY KEY (AccountID),
    CONSTRAINT UQ_Account_Email UNIQUE (Email)
);
GO

-- ============================================================
-- Table: Car
-- ============================================================
IF OBJECT_ID('dbo.Car', 'U') IS NOT NULL DROP TABLE dbo.Car;
CREATE TABLE dbo.Car (
    CarID         INT             IDENTITY(1,1) NOT NULL,
    CarName       NVARCHAR(200)   NOT NULL,
    CarModelYear  INT             NOT NULL,
    Color         NVARCHAR(50)    NOT NULL,
    Capacity      INT             NOT NULL,
    Description   NVARCHAR(1000)  NOT NULL,
    ImportDate    DATE            NOT NULL,
    ProducerID    INT             NOT NULL,
    RentPrice     DECIMAL(10,2)   NOT NULL,
    Status        NVARCHAR(10)    NOT NULL,
    CONSTRAINT PK_Car PRIMARY KEY (CarID),
    CONSTRAINT FK_Car_CarProducer FOREIGN KEY (ProducerID)
        REFERENCES dbo.CarProducer(ProducerID)
);
GO

CREATE INDEX IX_Car_ProducerID ON dbo.Car(ProducerID);
GO

-- ============================================================
-- Table: Customer
-- ============================================================
IF OBJECT_ID('dbo.Customer', 'U') IS NOT NULL DROP TABLE dbo.Customer;
CREATE TABLE dbo.Customer (
    CustomerID     INT           IDENTITY(1,1) NOT NULL,
    FullName       NVARCHAR(200) NOT NULL,
    Mobile         VARCHAR(15)   NOT NULL,
    Birthday       DATE          NOT NULL,
    IdentityCard   VARCHAR(20)   NOT NULL,
    LicenceNumber  VARCHAR(20)   NOT NULL,
    LicenceDate    DATE          NOT NULL,
    AccountID      INT           NOT NULL,
    CONSTRAINT PK_Customer PRIMARY KEY (CustomerID),
    CONSTRAINT UQ_Customer_IdentityCard UNIQUE (IdentityCard),
    CONSTRAINT UQ_Customer_LicenceNumber UNIQUE (LicenceNumber),
    CONSTRAINT UQ_Customer_Mobile UNIQUE (Mobile),
    CONSTRAINT FK_Customer_Account FOREIGN KEY (AccountID)
        REFERENCES dbo.Account(AccountID)
);
GO

CREATE INDEX IX_Customer_AccountID ON dbo.Customer(AccountID);
GO

-- ============================================================
-- Table: CarRental
-- ============================================================
IF OBJECT_ID('dbo.CarRental', 'U') IS NOT NULL DROP TABLE dbo.CarRental;
CREATE TABLE dbo.CarRental (
    CarRenID     INT           IDENTITY(1,1) NOT NULL,
    CustomerID   INT           NOT NULL,
    CarID        INT           NOT NULL,
    PickupDate   DATE          NOT NULL,
    ReturnDate   DATE          NOT NULL,
    RentPrice    DECIMAL(10,2) NOT NULL,
    Status       NVARCHAR(10)  NOT NULL,
    CONSTRAINT PK_CarRental PRIMARY KEY (CarRenID),
    CONSTRAINT FK_CarRental_Customer FOREIGN KEY (CustomerID)
        REFERENCES dbo.Customer(CustomerID),
    CONSTRAINT FK_CarRental_Car FOREIGN KEY (CarID)
        REFERENCES dbo.Car(CarID),
    CONSTRAINT CK_CarRental_DateRange CHECK (ReturnDate > PickupDate)
);
GO

CREATE INDEX IX_CarRental_CustomerID ON dbo.CarRental(CustomerID);
CREATE INDEX IX_CarRental_CarID ON dbo.CarRental(CarID);
CREATE INDEX IX_CarRental_PickupDate ON dbo.CarRental(PickupDate);
GO

-- ============================================================
-- Table: Review
-- ============================================================
IF OBJECT_ID('dbo.Review', 'U') IS NOT NULL DROP TABLE dbo.Review;
CREATE TABLE dbo.Review (
    ID          INT           IDENTITY(1,1) NOT NULL,
    CarRenID    INT           NOT NULL,
    ReviewStar  INT           NOT NULL,
    Comment     NVARCHAR(500) NOT NULL,
    CONSTRAINT PK_Review PRIMARY KEY (ID),
    CONSTRAINT FK_Review_CarRental FOREIGN KEY (CarRenID)
        REFERENCES dbo.CarRental(CarRenID),
    CONSTRAINT CK_Review_Star CHECK (ReviewStar BETWEEN 1 AND 5)
);
GO

CREATE INDEX IX_Review_CarRenID ON dbo.Review(CarRenID);
GO

-- ============================================================
-- Seed data: admin/admin account
-- Password hash is BCrypt(10) of 'admin'
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE Email = 'admin')
BEGIN
    INSERT INTO dbo.Account (AccountName, Email, Password, Role)
    VALUES (N'admin', N'admin', N'$2a$10$p9nZqLJZK4tLGjdqMKOM4.iDUuch92yPU36gITXJfYqwlRaCuDhLG', N'ADMIN');
END
GO

PRINT 'FUCarRentingSystem_DB created successfully.';
PRINT 'Admin account: email=admin / password=admin';
GO