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
<adjective>-<color>-<cyber security term>
```

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
| alias.retries           | 10                        | Number of retries for auto-generated alias. Must be unique within the CTFd instance. |
| ctfd.info.name          | Fetch 2023                | The event name                                                                       |
| ctfd.info.url           | https://fetch2023.snyk.io | The url for the event                                                                |
| ctfd.api.token          | ctfd_....                 | The token created on the CTFd platform for use with its API                          |
| ctfd.api.base-url       | https://fetch2023.snyk.io | The base url for the CTFd instance                                                   |
| ctfd.api.affiliation    | fetch2023                 | An affiliation value that will automatically be assigned to all new users            |
| ctfd.api.email-template | {"text": "..."}           | A template used to send credential info to users                                     |
