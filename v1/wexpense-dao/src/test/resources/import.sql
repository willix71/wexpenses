insert into currency (code,name) values('GBP', 'British Pounds');
insert into currency (code,name) values('CHF', 'Swiss Francs');
insert into currency (code,name) values('USD', 'US Dollars');
insert into currency (code,name) values('EUR', 'Euros');

insert into country (code,name,currency) values('CH', 'Switzerland', 'CHF');
insert into country (code,name,currency) values('FR', 'France', 'EUR');
insert into country (code,name,currency) values('IT', 'Italy', 'CHF');
insert into country (code,name,currency) values('DE', 'Germany', 'EUR');
insert into country (code,name,currency) values('US', 'United States', 'USD');
insert into country (code,name,currency) values('UK', 'United Kingdom', 'GBP');

insert into city (created_ts, modified_ts, country, name, zip, display, uid) values ('2011-08-10 23:44:35',  '2011-08-10 23:45:50', 'CH', 'Prangins', '1197', 'prangins (CH)', '88d8fb35-5c80-46fa-8378-d4f9c0cd637c');
insert into city (created_ts, modified_ts, country, name, zip, display, uid) values ('2011-08-10 23:46:37',  '2011-08-10 23:46:45', 'CH', 'Gland', '1196', 'gland (CH)', '4bb2c36e-39d5-4e68-bd15-5683d2085961');
insert into city (created_ts, modified_ts, country, name, zip, display, uid) values ('2011-08-10 23:56:17',  '2011-08-10 23:56:31', 'CH', 'Nyon', '1260', 'nyon (CH)', '91830157-b410-4a08-822c-4c13abaff925');
insert into city (created_ts, modified_ts, country, name, zip, display, uid) values ('2011-08-10 23:56:48',  '2011-08-10 23:56:53', 'UK', 'London', '', 'london (UK)', 'bda986a8-a4a1-4d5e-b126-a99ee7306efb');
insert into city (created_ts, modified_ts, country, name, zip, display, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'UK', 'Liverpool', '', 'liverpool (UK)', '3fc7a58c-551f-46b7-a79c-0afa32346976');

insert into account (created_ts, modified_ts, name, full_name, number, full_number, type, currency, selectable, parent, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'assets', 'assets', '1', '1', 'ASSET', null, false, null, 'a968a5f2-f9a4-4f74-89dc-85b66331972a')
insert into account (created_ts, modified_ts, name, full_name, number, full_number, type, currency, selectable, parent, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'cash', 'cash', '1', '11', 'ASSET', 'CHF', true, 'a968a5f2-f9a4-4f74-89dc-85b66331972a', '6860d5ab-0aea-4f15-95f2-6146f314d806')
insert into account (created_ts, modified_ts, name, full_name, number, full_number, type, currency, selectable, parent, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'expenses', 'expenses', '3', '3', 'EXPENSE', null, false, null, '1b25bb3a-135f-44f6-9731-6da09ee88ae8')
insert into account (created_ts, modified_ts, name, full_name, number, full_number, type, currency, selectable, parent, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'unknown', 'unknown', '9', '39', 'EXPENSE', 'CHF', true,'1b25bb3a-135f-44f6-9731-6da09ee88ae8', '0cb9bc3d-fe9d-4c8d-946e-bb1e5376b858')

insert into payee_type (created_ts, modified_ts, name, selectable, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'Pub', true,'1dfd6465-1909-41d8-925c-7ceccd4b03d8');
insert into payee_type (created_ts, modified_ts, name, selectable, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 'Pharmacie', true,'a2c5ff64-fdbe-48b6-9dc5-97874b1a99cd');

insert into payee (created_ts, modified_ts, type, prefix, name, location, city, external_reference, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', '1dfd6465-1909-41d8-925c-7ceccd4b03d8' ,' ', 'Bulls', null, '4bb2c36e-39d5-4e68-bd15-5683d2085961', null, '1dfd6465-1909-41d8-925c-7ceccd4b03d8');
 
insert into expense (created_ts, modified_ts, amount, currency, date , description , external_reference, payee, type, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 20.00 ,'CHF', '2012-03-10 23:56:46', null, null, '1dfd6465-1909-41d8-925c-7ceccd4b03d8',null,'d4f5d26b-86e1-4440-a8f2-10c51e379423');

-- insert into transaction_line (created_ts, modified_ts, amount, factor, value, account, period, consolidated_date, consolidation, exchange_rate, expense, uid) 
insert into transaction_line (created_ts, modified_ts, amount, factor, amount_value, account, period, expense, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 20, -1, 20000, '6860d5ab-0aea-4f15-95f2-6146f314d806', 1000, 'd4f5d26b-86e1-4440-a8f2-10c51e379423','9eaf3ff1-34df-4d2c-bbfb-84661ee56faf');
insert into transaction_line (created_ts, modified_ts, amount, factor, amount_value, account, period, expense, uid) values ('2011-08-10 23:56:33',  '2011-08-10 23:56:46', 20, 1, 20000, '0cb9bc3d-fe9d-4c8d-946e-bb1e5376b858', 1000, 'd4f5d26b-86e1-4440-a8f2-10c51e379423','10014e0c-2db8-4903-91cf-3ef170b72015');
