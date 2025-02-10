# Database

Common issues faced when designing database and using jdbc for mapping `Java` objects to database tables.

## JDBC and Database Mappings

By default, H2 and presumably most other SQL databases like MySql and Oracle converts all identifiers such as
table name, column name etc. to *UPPERCASE*.

`JDBC` knows this and takes care of such conversion when mapping `Java` objects annotated with `@Table` to database
tables.
Most problems related to missing table or SQL syntax error occurs when explicitly specifying identifier names via
`@Table("table_name")`, `@Column(name="something")` annotations.

In this case we explicitly define the identifiers in a case-sensitive way using double-quotes `"`, so JDBC treats
them with honor without making any changes.

### If you still need to customize mapping names when working with JDBC, ensure the following:

> Note: You might need to do all depending on your use case

- Automatic case conversion is turned off via appropriate setting or configuration. Example for H2 database,
  we can specify `DATABASE_TO_UPPER=false` in the connection URL

```
spring.datasource.url=jdbc:h2:mem:db_name;DATABASE_TO_UPPER=false
```

- Ensure the `Java` object being mapped to a specific table is annotated completely. **Do Not** annotate only few
  fields with `@Column("name")`, as JDBC will use default strategy and convert non-annotated fields to upper case
  and for the rest will use explicit case-sensitive identifiers you defined, leading to a mismatch identifiers in SQL
  statements.

- Ensure your DDL matches the case of the identifiers you intend to use.

### References

- [H2 Database Settings](https://h2database.com/javadoc/org/h2/engine/DbSettings.html#databaseToUpper)
- [Spring data JDBC adds double quotes - StackOverflow](https://stackoverflow.com/a/78115246)
- [Spring migration guide to JDBC 2.0](https://spring.io/blog/2020/05/20/migrating-to-spring-data-jdbc-2-0)
- [Difference between Single and Double quotes - StackOverflow](https://stackoverflow.com/a/13089425)
- [Quotes and backticks in SQL](https://www.atlassian.com/data/sql/single-double-quote-and-backticks-in-mysql-queries)

### TL;DR

- Respect JDBC's default mapping for database identifiers and use `@Table` and `@Column` annotations without customizing
  names.

**OR**

- Since JDBC always puts identifiers within double quotes, make sure your `name` in `@Column("name")` equals the
  `name` you gave your identifiers in your database, respecting the **case-sensitivity** of the database.

## Storing Timestamps

Using `TIMESTAMP` datatype in database, when passing timestamp, it gets converted to the timezone of the database.

> Note: Tested on H2 in memory db with default settings

- If we input a timestamp in UTC `2025-01-27T11:18:05.142285700Z`, to database column of type `TIMESTAMP`,
  the persisted value in database becomes `2025-01-27 16:48:05.142286`, which is the time with an offset of
  `+05:30` applied.

> Note: `+05:30` is the offset of the system timezone where the database is running.

Similarly, if we specify an offset in the input, the persisted value gets updated to match the database timezone:

| Input timestamp                       | Persisted timestamp          |
|---------------------------------------|------------------------------|
| 2025-01-27T11:18:05.142285700+00:00   | 2025-01-27 16:48:05.142286   |
| `2025-01-27T11:18:05.142285700+05:30` | `2025-01-27 11:18:05.142286` |
| 2025-01-27T11:18:05.142285700+07:30   | 2025-01-27 09:18:05.142286   |
| 2025-01-27T11:18:05.142285700-04:00   | 2025-01-27 20:48:05.142286   |

> Note: In second example since the input offset of `+05:30` matched the database timezone, no conversion was applied

### To fix this, we can use `TIMESTAMP WITH TIME ZONE` for the database column

This will persist the input timestamp as `2025-01-27 11:18:05.142286+00`, which is correct and preferable specially in
case we can't remember whether we pass the timestamp as UTC or local, we can check the offset. If however the ending
offset `+00` (which is redundant when we know the timestamp will always be in `UTC`) is bothering you, we can instead
try to set the database timezone to be in UTC.

In **H2** we can pass `TIME ZONE=UTC` to the connection url:

```
spring.datasource.url=jdbc:h2:mem:dev_db;TIME ZONE=UTC
```

then the persisted timestamp doesn't get converted and is stored as: `2025-01-27 11:18:05.142286`.

### Using `TIMESTAMP WITH TIME ZONE` in database

> Do not use (at least avoid using) Instant API with JDBC or JPA.

When using `Instant` API in `Java` for timestamps in JDBC, input timestamps in UTC gets converted to database timezone
similar to `TIMESTAMP` fields we saw earlier, but with offsets applied and stored in database.
Essentially converting the `Instant` timestamp in `UTC` to a `OffsetDateTime` with offset of
`+05:30` (the db timezone offset).

This will look familiar to [Storing Timestamps](#Storing-Timestamps) above, but since it's a
`WITH TIME ZONE` field in database, the offset applied is also stored.

Example

| Input timestamp                | Persisted timestamp              |
|--------------------------------|----------------------------------|
| 2025-01-27T11:18:05.142285700Z | 2025-01-27 16:48:05.142286+05:30 |

### TL;DR

If we truly need to persist UTC timestamp **as-is** and ensure no db conversion or mapping issue occur.
Use `OffsetDateTime` API with `TIMESTAMP WITH TIME ZONE` db column.

Example

| Input timestamp                | Persisted timestamp           |
|--------------------------------|-------------------------------|
| 2025-01-27T11:18:05.142285700Z | 2025-01-27 11:18:05.142286+00 |