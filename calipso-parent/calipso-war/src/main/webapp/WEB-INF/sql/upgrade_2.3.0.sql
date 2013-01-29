ALTER TABLE `custom_attributes` ADD `ca_version` INT NOT NULL DEFAULT '0';

ALTER TABLE `attributes_lookup_values` ADD `listIndex` INT NOT NULL DEFAULT '0';
ALTER TABLE `attributes_lookup_values` ADD `lkv_active` BIT( 1 ) NULL DEFAULT b'1';
ALTER TABLE `spaces` ADD `published` BIT( 1 ) NOT NULL DEFAULT b'0';
