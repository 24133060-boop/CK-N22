# UTEMILKTEA v0.5 - Sua loi SQLState 22005

Da bo `@Nationalized` tren cac thuoc tinh String vi trong ban v0.4, anh xa Hibernate/JDBC bi xung dot voi kieu du lieu cua SQL Server, va khai bao cot `NVARCHAR(255)` ro rang. Giu getter cho JSP.

## Thuc hien
1. Sao luu UTEMILKTEA_DB trong SSMS (neu co du lieu quan trong).
2. Neu chua chay script v0.4, chay `database/02-fix-vietnamese.sql` de sua du lieu mau da bi mat dau.
3. Chay `database/03-fix-unicode-mapping.sql` tren database UTEMILKTEA_DB. Neu loi ALTER COLUMN, chup thong bao SSMS de kiem tra rang buoc/chi muc.
4. Dung ung dung cu, import Maven project moi trong STS, Maven > Update Project, Run As > Spring Boot App.
5. Kiem tra trang chu, tim kiem, dang nhap va dat hang.

SQL Server port mac dinh trong config la 14330. Cau hinh mat khau qua bien moi truong `DB_PASSWORD`; khong commit mat khau vao GitHub.

Luu y: Da kiem tra ma nguon va file ZIP; chua kiem thu ket noi SQL Server tren may cua ban.
