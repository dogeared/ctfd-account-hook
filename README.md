![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)
[![Java CI](https://github.com/dogeared/ctfd-account-hook/actions/workflows/main.yml/badge.svg)](https://github.com/dogeared/ctfd-account-hook/actions/workflows/main.yml)

# CTFd Account Manager

This is an API service that interacts with the CTFd API for specific use cases.

The primary use case is supporting registration by an external service with and without notification.

For instance, for an upcoming CTF event, a company might want to use a formatted registration landing page such as
those set up in Marketo.

Further, the event may be several weeks or months away and the company running the event might not want registered 
users to be notified from CTFd with their account credentials until just before the event.

## Test Environment

The test environment uses the embedded H2 database. Because of security concerns, it is ONLY scoped to `test` and is
not available in any runtime environment

To run the test suite:

```
mvn clean install
```

## Runtime Environment

While there is nothing specific to MySQL in the code, the `pom.xml` calls out the MySQL driver. The 
`application-sample.yml` has examples of the properties you need to set to connect to the database.

To run the application:

```
mvn spring-boot:run
```

## Database prep

In order for an API Key to work, you'll need to add it's BCrypt hashed value to the `api_key` table of the database.

A quick way to do this is using the site: https://bcrypt-generator.com/

When you have a BCrypt hash, you can insert it into your database like so:

```
INSERT INTO 
    api_key 
VALUES
    (unhex(replace(uuid(), "-","")), 'local', '2024-10-24', '<bcrypt hash>', 0);
```

The table key is a `uuid` value. Then, there's description, expiration date, bcrypt hash and an `is_revoked` field.

You can confirm that the API is working with the hello endpoint as follows:

```
curl \
localhost:8080/hello-world \
-H "x-api-key: <api key value>
```

Note: the `<api key value>` above should be the unhashed, raw value you used to create the BCrypt hash earlier.

## Supported API endpoints

All endpoints take JSON inputs and produce JSON output.
Some endpoints have request parameters as well.

All endpoints require a configured HTTP request header name and shared secret value. See Configuration section below
for more information.

### POST /api/v1/users

This endpoint is used to create a new user in the configured CTFd instance.

Request Body:

| JSON field | Required | Type    | Default |
|------------|----------|---------|---------|
| email      | Yes      | String  |         |
| notify     | No       | Boolean | false   |

Example (Using HTTPie):

```
http POST https://my-ctfd-account-hook.server/api/v1/users \
"X-API-KEY":"shared-secret" \
--raw '{"email": "user@example.com"}'
```

The created user will have a CTFd `Name` assigned to them. This `Name` has the following format:

```
<adjective>-<color>-<configured dictionary term>
```

The first two sections of the alias are always an adjective and a color, respectively. The third section is
configurable based on the setting of the environment variable: `api.dictionary`. Valid values are:

| api.dictionary | Description          |
|----------------|----------------------|
| CYBER          | cyber security terms |
| DOGS           | dog breeds           |

`CYBER` is the default dictionary in case `api.dictionary` is set to an invalid value or is null.

The dictionaries used for all sections of an alias are defined in the 
[AliasService](src/main/java/dev/dogeared/ctfdaccounthook/service/AliasService.java) interface.

**NOTE**: The `affiliation` field for the user is automatically set based on the configuration. See below for more on
configuration.

### GET /api/v1/users

This endpoint returns all users by `affiliation`.

Request Params:

| Param       | Required | Default                      |
|-------------|----------|------------------------------|
| affiliation | No       | ctfd.api.affiliation env var |
| page        | No       | 1                            |

### POST /api/v1/update-and-email/{affiliation}

This endpoint is used to send an email to all users that belong to the named `affiliation`.

For each user in the result set, a random UUID is set as the password and an email is sent via the CTFd API with the
user's credentials.

## Configuration

The following environment variables are required to configure the service.

| Env Var                 | Example Value             | Description                                                                          |
|-------------------------|---------------------------|--------------------------------------------------------------------------------------|
| api.auth.header-name    | X-API-KEY                 | The header name used for authentication to the service                               |
| api.auth.token          | super-secret              | The shared secret used on every request of the service                               |
| api.dictionary          | CYBER                     | Which dictionary to use for the alias. Valid values are: CYBER (default) or DOGS     | 
| alias.retries           | 10                        | Number of retries for auto-generated alias. Must be unique within the CTFd instance. |
| ctfd.info.name          | Fetch 2023                | The event name                                                                       |
| ctfd.info.url           | https://fetch2023.snyk.io | The url for the event                                                                |
| ctfd.api.token          | ctfd_....                 | The token created on the CTFd platform for use with its API                          |
| ctfd.api.base-url       | https://fetch2023.snyk.io | The base url for the CTFd instance                                                   |
| ctfd.api.affiliation    | fetch2023                 | An affiliation value that will automatically be assigned to all new users            |
| ctfd.api.email-template | {"text": "..."}           | A template used to send credential info to users                                     |
