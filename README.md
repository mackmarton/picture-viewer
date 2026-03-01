# 📷 Fényképalbum Alkalmazás
![](UI.png)

## Felhasználói dokumentáció

**Bejelentkezés nélkül:**
- A főoldalon látható az összes feltöltött kép
- A képek felett egy legördülő menüből választható a rendezés (név vagy dátum szerint)
- Egy képre kattintva megjelenik a nagyobb nézet a név és feltöltési dátum részletekkel
- Feltöltés és törlés funkciók nem érhetők el

**Bejelentkezés után:**
- A jobb felső sarokban megjelenik a felhasználónév és a "Kilépés" gomb
- A képlista felett megjelenik egy "Új kép feltöltése" kártya
- Kép feltöltésekor meg kell adni a kép nevét (maximum 40 karakter) és ki kell választani a fájlt
- A részletes képnézetben megjelenik a "Törlés" gomb, amellyel a kép eltávolítható

**Regisztráció:**
- A jobb felső sarokban lévő "Bejelentkezés" linkre kattintva, alul található a regisztrációs lehetőség
- Felhasználónév és jelszó megadása szükséges (felhasználónév maximum 20 karakter)

## Architektúra dokumentáció

### Technológia Stack és szolgáltatók

- **Backend:** Java Spring Boot
- **Adatbázis:** PostgreSQL a Neon providernél
- **Képtárolás:** Cloudinary (CDN)
- **Frontend:** JavaScript Single Page Application (Egyszerű hostolás miatt)
- **PaaS szolgáltató:** Heroku

### API Végpontok

**Autentikáció:**
- `POST /api/auth/register` - Regisztráció
- `POST /api/auth/login` - Bejelentkezés
- `POST /api/auth/logout` - Kilépés
- `GET /api/auth/me` - Jelenlegi felhasználó

**Képek:**
- `GET /api/pictures` - Összes kép listázása (opcionális `sort` paraméter: `name` vagy `date`)
- `GET /api/pictures/{id}` - Kép részletei
- `POST /api/pictures` - Új kép feltöltése (csak bejelentkezve)
- `DELETE /api/pictures/{id}` - Kép törlése (csak bejelentkezve)

**Fronend:**
- `GET /` - Maga a felhasználói felület

### Biztonsági megoldások

Az alkalmazás biztosítja az adatok és felhasználók védelmét. A jelszavak tárolása egy hash algoritmussal történik, így a jelszavak soha nem kerülnek plain text formában tárolásra. A kommunikáció a Heroku -n való futtatásnak köszönhetően tikosítva, HTTPS protokollon keresztül történik.

Az autorizáció gondoskodik arról, hogy csak bejelentkezett felhasználók tölthessenek fel vagy törölhessenek képeket.

A bemenetek backend és frontend oldalon is validálásra kerülnek. 

### Heroku konfiguráció

Az alkalmazás Herokuval való összekötéséhez nincs más dolgunk mint a Heroku dashboardon kiválasztani a megfelelő GitHub repository-t majd beállítani a következő környezeti változókat (Config Vars):
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

A `PORT` változó beállítására nincs szükség mivel a Heroku automatikusan beállítja azt.

### Build és Deploy folyamat

Az alkalmazás buildeléséért és telepítéséért a Heroku felelős. Automatikusan észleli a main branchre való push -t és elkezdi a buildet. Amennyiben az sikeresen végződik az alkalmazás telepítésre kerül és elérhető a heroku által megadott URL -n.
