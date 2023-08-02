create table ygroup (
  id integer generated by default as identity constraint pk_ygroup primary key,
  groupname varchar(50) character set UTF8 not null constraint uq_ygroup_name unique collate UNICODE_CI
);

create table rawdata (
  group_id integer not null constraint fk_rawdata_ygroup references ygroup (id),
  message_id integer not null,
  message_json blob sub_type text character set utf8,
  raw_message_json blob sub_type text character set utf8,
  last_update timestamp default current_timestamp not null,
  constraint pk_rawdata primary key (group_id, message_id)
);

create table link_info (
  group_id integer not null,
  message_id integer not null,
  y_topic_id integer,
  y_prev_in_topic integer,
  y_prev_in_time integer,
  post_date timestamp,
  post_year smallint generated always as (extract(year from post_date)),
  post_month smallint generated always as (extract(month from post_date)),
  constraint pk_link_info primary key (group_id, message_id),
  constraint fk_link_info_rawdata foreign key (group_id, message_id) references rawdata (group_id, message_id)
);

CREATE VIEW POST_INFORMATION
AS
SELECT
  a.GROUP_ID,
  a.MESSAGE_ID,
  a.GROUPNAME,
  a.POST_DATE,
  a.POST_YEAR,
  a.POST_MONTH,
  a.TOPIC_ID,
  topic.POST_YEAR AS TOPIC_YEAR,
  topic.POST_MONTH AS TOPIC_MONTH,
  a.PREV_IN_TOPIC,
  prev_in_topic.POST_YEAR AS PREV_IN_TOPIC_YEAR,
  prev_in_topic.POST_MONTH AS PREV_IN_TOPIC_MONTH,
  a.NEXT_IN_TOPIC,
  next_in_topic.POST_YEAR AS NEXT_IN_TOPIC_YEAR,
  next_in_topic.POST_MONTH AS NEXT_IN_TOPIC_MONTH,
  a.PREV_IN_TIME,
  prev_in_time.POST_YEAR AS PREV_IN_TIME_YEAR,
  prev_in_time.POST_MONTH AS PREV_IN_TIME_MONTH,
  a.NEXT_IN_TIME,
  next_in_time.POST_YEAR AS NEXT_IN_TIME_YEAR,
  next_in_time.POST_MONTH AS NEXT_IN_TIME_MONTH,
  a.MESSAGE_JSON
FROM (
    SELECT
      GROUP_ID,
      MESSAGE_ID,
      g.GROUPNAME,
      -- Using the MESSAGE_ID as an indication of time ordering between messages, not the POST_DATE
      FIRST_VALUE(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS TOPIC_ID,
      LAG(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS PREV_IN_TOPIC,
      LEAD(MESSAGE_ID) OVER (PARTITION BY GROUP_ID, li.Y_TOPIC_ID ORDER BY MESSAGE_ID) AS NEXT_IN_TOPIC,
      LAG(MESSAGE_ID) OVER (PARTITION BY GROUP_ID ORDER BY MESSAGE_ID) AS PREV_IN_TIME,
      LEAD(MESSAGE_ID) OVER (PARTITION BY GROUP_ID ORDER BY MESSAGE_ID) AS NEXT_IN_TIME,
      POST_DATE,
      POST_YEAR,
      POST_MONTH,
      r.MESSAGE_JSON
    FROM LINK_INFO li
    INNER JOIN RAWDATA r USING (GROUP_ID, MESSAGE_ID)
    INNER JOIN YGROUP g ON g.ID = GROUP_ID
) a
INNER JOIN LINK_INFO topic ON topic.GROUP_ID = a.GROUP_ID AND topic.MESSAGE_ID = a.TOPIC_ID
LEFT JOIN LINK_INFO prev_in_topic ON prev_in_topic.GROUP_ID = a.GROUP_ID AND prev_in_topic.MESSAGE_ID = a.PREV_IN_TOPIC
LEFT JOIN LINK_INFO next_in_topic ON next_in_topic.GROUP_ID = a.GROUP_ID AND next_in_topic.MESSAGE_ID = a.NEXT_IN_TOPIC
LEFT JOIN LINK_INFO prev_in_time ON prev_in_time.GROUP_ID = a.GROUP_ID AND prev_in_time.MESSAGE_ID = a.PREV_IN_TIME
LEFT JOIN LINK_INFO next_in_time ON next_in_time.GROUP_ID = a.GROUP_ID AND next_in_time.MESSAGE_ID = a.NEXT_IN_TIME;

create view sitemap_links as
select
  cast('/' || y.GROUPNAME || '/index.html' as varchar(50)) as path,
  LOCALTIMESTAMP as last_change
from YGROUP y
union all
select
  cast('/' || y.GROUPNAME || '/' || l.POST_YEAR || '/' || l.POST_MONTH || '/index.html' as varchar(50)) as path,
  (select max(POST_DATE) from LINK_INFO li where li.GROUP_ID = l.GROUP_ID AND li.POST_YEAR = l.POST_YEAR AND li.POST_MONTH = l.POST_MONTH) as last_change
from (select distinct GROUP_ID, POST_YEAR, POST_MONTH from LINK_INFO) l
inner join YGROUP y on y.ID = l.GROUP_ID
union all
select
  cast('/' || y.GROUPNAME || '/' || l.POST_YEAR || '/' || l.POST_MONTH || '/' || l.MESSAGE_ID || '.html' as varchar(50)) as path,
  l.POST_DATE as last_change
from LINK_INFO l
inner join YGROUP y on y.ID = l.GROUP_ID;
