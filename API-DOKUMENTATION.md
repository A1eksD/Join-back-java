# API-Dokumentation – Join

> **Base-URL:** `http://localhost:4200/api`
> **Content-Type:** `application/json`
> **Authentifizierung:** Firebase Auth (Login/Register via SDK) — Datenbankzugriffe ohne Token

---

## Aufteilung 50 / 50

| Service | Methode | Technologie |
|---|---|---|
| `tasks.service.ts` | Alle Task-Operationen | `fetch` + `async/await` |
| `users.service.ts` | Alle Kontakt-Operationen | `HttpClient` + `Observable` (RxJS) |
| `login.service.ts` | User anlegen / suchen / Status | `HttpClient` + `Observable` (RxJS) |
| `login.service.ts` | Login / Register / Google / Logout | Firebase Auth SDK (unverändert) |

---

## Tasks — `fetch` / `async-await`

### `GET /api/tasks`
Alle Tasks laden.

**Response**
```json
[
  {
    "id": "abc123",
    "title": "Design Mockup",
    "description": "Erstelle UI-Mockup für Login",
    "date": 1711670400,
    "priority": "medium",
    "category": "inProgress",
    "publishedTimestamp": 1711670000,
    "createtBy": "userId123",
    "assignetTo": ["userId456"],
    "subtasks": ["Wireframe fertig", "Review ausstehend"]
  }
]
```

---

### `POST /api/tasks`
Neuen Task anlegen.

**Request-Body**
```json
{
  "title": "Design Mockup",
  "description": "Erstelle UI-Mockup für Login",
  "date": 1711670400,
  "priority": "medium",
  "category": "todo",
  "publishedTimestamp": 1711670000,
  "createtBy": "userId123",
  "assignetTo": ["userId456"],
  "subtasks": ["Wireframe fertig"]
}
```

**Response** — angelegtes Task-Objekt mit `id`
```json
{
  "id": "newTaskId",
  "title": "Design Mockup",
  "..."  : "..."
}
```

---

### `PATCH /api/tasks/:id`
Task vollständig aktualisieren.

**Request-Body** — vollständiges Task-Objekt (wie POST)

---

### `PATCH /api/tasks/:id/category`
Nur die Kategorie eines Tasks aktualisieren (z.B. beim Drag & Drop).

**Request-Body**
```json
{
  "category": "done"
}
```

---

### `PATCH /api/tasks/:id/subtasks`
Nur die Subtasks eines Tasks aktualisieren.

**Request-Body**
```json
{
  "subtasks": ["Subtask 1 erledigt", "Subtask 2 offen"]
}
```

---

### `DELETE /api/tasks/:id`
Task löschen.

**Response**
```json
{}
```

---

## Users — `HttpClient` / RxJS

### `GET /api/users`
Alle registrierten User laden.

**Response**
```json
[
  {
    "id": "userId123",
    "uid": "firebaseUid_abc",
    "firstName": "Max",
    "lastName": "Mustermann",
    "email": "max@example.com",
    "status": true,
    "color": "#ff5733",
    "phoneNumber": 491234567890,
    "savedUsers": [
      {
        "uid": "contactUid_xyz",
        "firstName": "Anna",
        "lastName": "Schmidt",
        "email": "anna@example.com",
        "status": false,
        "color": "#42a5f5"
      }
    ]
  }
]
```

---

### `GET /api/users?uid=:firebaseUid`
User per Firebase UID suchen (wird nach Login/Google-Login aufgerufen).

**Response** — Array mit einem oder keinem User (gleiche Struktur wie `GET /api/users`)

---

### `GET /api/common-users`
Alle gemeinsamen Kontakte laden (für Suche & geteilte Kontakte).

**Response**
```json
[
  {
    "id": "commonUserId",
    "savedUsers": [
      {
        "uid": "contactUid_xyz",
        "firstName": "Anna",
        "lastName": "Schmidt",
        "email": "anna@example.com",
        "status": false,
        "color": "#42a5f5"
      }
    ]
  }
]
```

---

### `POST /api/users/:userId/contacts`
Neuen Kontakt zur `savedUsers`-Liste des eingeloggten Users hinzufügen.

**Request-Body** — das neue Kontakt-Objekt
```json
{
  "uid": "newContactUid",
  "firstName": "Lisa",
  "lastName": "Meier",
  "email": "lisa@example.com",
  "status": false,
  "color": "#66bb6a",
  "phoneNumber": 491234567890,
  "savedUsers": []
}
```

---

### `POST /api/common-users`
Kontakt gleichzeitig in der gemeinsamen Kontaktliste anlegen.

**Request-Body**
```json
{
  "savedUsers": [
    {
      "uid": "newContactUid",
      "firstName": "Lisa",
      "lastName": "Meier",
      "email": "lisa@example.com",
      "status": false,
      "color": "#66bb6a"
    }
  ]
}
```

---

### `PATCH /api/users/:userId/contacts/:contactUid`
Bestehenden Kontakt bearbeiten.

**Request-Body** — aktualisiertes Kontakt-Objekt
```json
{
  "uid": "contactUid_xyz",
  "firstName": "Anna",
  "lastName": "Schmidt-Neu",
  "email": "anna.neu@example.com",
  "status": false,
  "color": "#42a5f5",
  "phoneNumber": 4987654321
}
```

---

### `PATCH /api/common-users/:commonUserId`
Dazugehörigen CommonUser-Eintrag aktualisieren (wird parallel zu `PATCH .../contacts/:uid` via `forkJoin` aufgerufen).

**Request-Body**
```json
{
  "savedUsers": [
    {
      "uid": "contactUid_xyz",
      "firstName": "Anna",
      "lastName": "Schmidt-Neu",
      "email": "anna.neu@example.com",
      "status": false,
      "color": "#42a5f5"
    }
  ]
}
```

---

### `DELETE /api/users/:userId/contacts/:contactUid`
Kontakt löschen.

**Response**
```json
{}
```

---

## Login / Auth — `HttpClient` / RxJS (Daten) + Firebase Auth (Authentifizierung)

### `POST /api/users`
Neuen User nach erfolgreicher Firebase-Registrierung in der Datenbank anlegen.

**Request-Body**
```json
{
  "uid": "firebaseUid_abc",
  "firstName": "Max",
  "lastName": "Mustermann",
  "email": "max@example.com",
  "status": true,
  "color": "",
  "savedUsers": []
}
```

**Response** — angelegter User mit `id`
```json
{
  "id": "newUserId",
  "uid": "firebaseUid_abc",
  "firstName": "Max",
  "lastName": "Mustermann",
  "email": "max@example.com",
  "status": true,
  "color": "",
  "savedUsers": []
}
```

---

### `PATCH /api/users/:userId/status`
Online-Status des Users setzen (bei Login, Logout und Session-Start).

**Request-Body (einloggen)**
```json
{ "status": true }
```

**Request-Body (ausloggen)**
```json
{ "status": false }
```

---

## Firebase Auth — SDK (unverändert)

Diese Flows laufen weiterhin über das Firebase Auth SDK und kommunizieren **nicht** mit dem lokalen Backend:

| Methode | Firebase SDK-Funktion |
|---|---|
| `register()` | `createUserWithEmailAndPassword()` |
| `login()` | `signInWithEmailAndPassword()` |
| `guestLogin()` | `signInWithEmailAndPassword()` (fester Gast-Account) |
| `googleLogin()` | `signInWithPopup(GoogleAuthProvider)` |
| `logout()` | `signOut()` |

---

## Collections / Routen – Gesamtübersicht

| Methode | Endpunkt | Beschreibung |
|---|---|---|
| `GET` | `/api/tasks` | Alle Tasks laden |
| `POST` | `/api/tasks` | Task anlegen |
| `PATCH` | `/api/tasks/:id` | Task vollständig aktualisieren |
| `PATCH` | `/api/tasks/:id/category` | Nur Kategorie aktualisieren |
| `PATCH` | `/api/tasks/:id/subtasks` | Nur Subtasks aktualisieren |
| `DELETE` | `/api/tasks/:id` | Task löschen |
| `GET` | `/api/users` | Alle User laden |
| `GET` | `/api/users?uid=:uid` | User per Firebase UID suchen |
| `POST` | `/api/users` | User anlegen |
| `PATCH` | `/api/users/:id/status` | Online-Status setzen |
| `POST` | `/api/users/:id/contacts` | Kontakt hinzufügen |
| `PATCH` | `/api/users/:id/contacts/:uid` | Kontakt bearbeiten |
| `DELETE` | `/api/users/:id/contacts/:uid` | Kontakt löschen |
| `GET` | `/api/common-users` | Alle gemeinsamen Kontakte laden |
| `POST` | `/api/common-users` | Gemeinsamen Kontakt anlegen |
| `PATCH` | `/api/common-users/:id` | Gemeinsamen Kontakt aktualisieren |
