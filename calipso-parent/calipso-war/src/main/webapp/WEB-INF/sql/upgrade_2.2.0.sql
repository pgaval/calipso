
create table spacerole_item_templates (spacerole_id bigint not null, template_id bigint not null, state_status smallint not null, primary key (spacerole_id, state_status)) type=InnoDB;
alter table spacerole_item_templates add index FK58026530D76A5516 (spacerole_id), add constraint FK58026530D76A5516 foreign key (spacerole_id) references space_roles (sr_id);
alter table spacerole_item_templates add index FK58026530B006BAE2 (template_id), add constraint FK58026530B006BAE2 foreign key (template_id) references rendering_templates (rt_id);

create table item_rendering_templates (rt_id bigint not null, description varchar(255) not null unique, space_id bigint not null, primary key (rt_id), unique (description, space_id)) type=InnoDB;
alter table item_rendering_templates add column priority smallint not null;

alter table item_rendering_templates add index FK8BCA65DA1AE81AB6 (space_id), add constraint FK8BCA65DA1AE81AB6 foreign key (space_id) references spaces (id);
alter table item_rendering_templates add index FK8BCA65DA4D08C41A (rt_id), add constraint FK8BCA65DA4D08C41A foreign key (rt_id) references rendering_templates (rt_id);
alter table spacerole_item_templates add index FK58026530BEC1EA0F (template_id), add constraint FK58026530BEC1EA0F foreign key (template_id) references item_rendering_templates (rt_id);

ALTER TABLE `spaces` ADD `detail_comment_enabled` BIT NOT NULL DEFAULT b'1' AFTER `titles_enabled`;

ALTER TABLE `spacerole_item_templates` CHANGE `state_status` `state_status` VARCHAR( 9 ) NOT NULL;
-- ALTER TABLE `item_rendering_templates` ADD `hide_overview` BIT NOT NULL DEFAULT b'0', ADD `hide_history` BIT NOT NULL DEFAULT b'0'
-- ALTER TABLE `space_languages` CHANGE `SL_SPACE_ID` `sl_space_id` BIGINT( 20 ) NOT NULL;
-- ALTER TABLE `space_languages` CHANGE `SL_LID` `sl_lid` VARCHAR( 2 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
-- ALTER TABLE `space_languages` CHANGE `SL_SORT_ORDER` `sl_sort_order` INT( 11 ) NOT NULL

create table space_templates (space_id bigint not null, elt bigint not null, priority integer not null, primary key (space_id, priority)) type=InnoDB
alter table space_templates add index FK638B9F401AE81AB6 (space_id), add constraint FK638B9F401AE81AB6 foreign key (space_id) references spaces (id)
alter table space_templates add index FK638B9F405548D7BC (elt), add constraint FK638B9F405548D7BC foreign key (elt) references item_rendering_templates (RT_ID)

alter table item_field_custom_attributes add column show_in_search_results bit default 0;
alter table item_field_custom_attributes add column html_description longtext;
alter table spaces add column closing_date datetime;
ALTER TABLE `item_rendering_templates` CHANGE `RT_ID` `rt_id` BIGINT( 20 ) NOT NULL;
