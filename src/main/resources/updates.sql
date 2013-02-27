ALTER table PayMENT {
	add column SELECTABLE boolean
}
update payment set selectable = false;

ALTER TABLE PayMENT {
	ALTER COLUMN selectable SET NOT NULL
}
