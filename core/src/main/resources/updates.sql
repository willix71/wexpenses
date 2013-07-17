
--====== Payment ======
ALTER table PayMENT add column SELECTABLE boolean;
update payment set selectable = false;
ALTER TABLE PayMENT ALTER COLUMN selectable SET NOT NULL;

-- allow the payment's date to be null (will be filled before generating the dta)
alter table payment alter column date date(8) null;

--====== Template ======
drop table Template if exists;
create table Template (id bigint generated by default as identity, createdTs timestamp, modifiedTs timestamp, uid varchar(255) not null unique, version bigint, amount decimal(19,2), description varchar(255), externalReference varchar(255), inFactor integer, outFactor integer, templateDescription varchar(255), templateMenu varchar(255), templateName varchar(255) not null, templateOrder integer, currency_code varchar(3), inAccount_id bigint, outAccount_id bigint, payee_id bigint, type_id bigint, primary key (id));

alter table Template add constraint FKB515309A164AB2AD foreign key (outAccount_id) references Account;
alter table Template add constraint FKB515309AD1CAEFC4 foreign key (inAccount_id) references Account;
alter table Template add constraint FKB515309AC5D7C03F foreign key (payee_id) references Payee;
alter table Template add constraint FKB515309A5B8BBB7 foreign key (type_id) references ExpenseType;
alter table Template add constraint FKB515309AF59EEBC7 foreign key (currency_code) references Currency;

--====== Transaction Line ======
alter table Transactionline add column value DECIMAL(19,2);
update TRANSACTIONLINE  set value = amountvalue / 100.0;
alter table Transactionline alter column value SET NOT NULL;

alter table Transactionline add column balance DECIMAL(19,2);

alter table Transactionline alter column consolidateddate rename to date;
update Transactionline set date = (select date from expense where expense.id = Transactionline.expense_id) where date is null;

--====== Payee ======
ALTER TABLE Payee add column iban varchar(255);  
ALTER TABLE Payee add column postalAccount varchar(255);

UPDATE PAYEE SET 
	postalAccount = case when position('-',externalreference)>0 then externalreference end ,  
	iban = case when position('-',externalreference)=0 then externalreference end ;	
update payee set externalreference = concat('IBAN:',iban) where iban is not null;
update payee set externalreference = concat('CP:',postalAccount) where postalAccount is not null;

--====== Exchange Rate ======
ALTER TABLE exchangerate add column fromcurrency_code varchar(3);  
ALTER TABLE exchangerate add column tocurrency_code varchar(3);
UPDATE exchangerate xr SET
	xr.fromcurrency_code = (SELECT top 1 currency_code FROM expense JOIN transactionline on expense.id = transactionline.expense_id where transactionline.exchangerate_id = xr.id),
	xr.tocurrency_code = (SELECT top 1 currency_code FROM account JOIN transactionline on account.id = transactionline.account_id where transactionline.exchangerate_id = xr.id);
-- make sure we don't have any null currencies
UPDATE exchangerate SET fromcurrency_code = 'CHF' where fromcurrency_code is null;
UPDATE exchangerate SET tocurrency_code = 'CHF' where tocurrency_code is null;
	
ALTER TABLE exchangerate alter column fromcurrency_code set not null;  
ALTER TABLE exchangerate alter column tocurrency_code set not null;

ALTER TABLE exchangerate drop column buycurrency;  
ALTER TABLE exchangerate drop column sellcurrency;

alter table ExchangeRate add constraint FK5F01D9C39C357D2C foreign key (toCurrency_code) references Currency;
alter table ExchangeRate add constraint FK5F01D9C3ADD4185D foreign key (fromCurrency_code) references Currency;

--====== Account ======
UPDATE ACCOUNT set currency_code = 'CHF' where currency_code is null;

ALTER TABLE ACCOUNT add column bankDetails_id bigint;
alter table Account add constraint FK1D0C220D96A05241 foreign key (bankDetails_id) references Payee;

--//////// v2 ////////

ALTER TABLE ACCOUNT alter column bankDetails_id rename to owner_id;

alter table CONSOLIDATION add column DELTABALANCE DECIMAL(19,2);

alter table Transactionline alter column amountvalue set null;