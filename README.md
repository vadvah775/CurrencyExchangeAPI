# Простой проект для работы с валютными курсами
Проект учебный и несет исключительно ознакомительную работу с JavaEE и Tomcat
Примеры запросов и ответов:
``http://localhost:8080/Currency_Exchange_war/exchangeRate/USDEUR
```json
{
	"baseCurrency": {
		"code": "USD",
		"fullName": "United States Dollar",
		"id": 2,
		"sign": "$"
	},
	"id": 2,
	"rate": 0.8548,
	"targetCurrency": {
		"code": "EUR",
		"fullName": "Euro",
		"id": 4,
		"sign": "€"
	}
}
```
``http://localhost:8080/Currency_Exchange_war/exchange?from=EUR&to=USD&amount=10
```json
{
	"baseCurrency": {
		"code": "USD",
		"fullName": "United States Dollar",
		"id": 2,
		"sign": "$"
	},
	"rate": 1.1699,
	"targetCurrency": {
		"code": "EUR",
		"fullName": "Euro",
		"id": 4,
		"sign": "€"
	},
	"amount": 10.0000,
	"convertedAmount": 11.6990
}
```
Для запуска проекта необходимо добавить в папку src/main/resources/application.properties, где будет вся необходимая информация для подключение к базе данных  
Пример:
```  
db.url=jdbc:postgresql://localhost:5432/currency_exchange  
db.driver=org.postgresql.Driver  
db.username=postgres  
db.password=123  
```  
База данных должна иметь следующую структуру
#### Таблица currencies

| Колонка  | Тип     | Комментарий                               |
| -------- | ------- | ----------------------------------------- |
| ID       | int     | Айди валюты, автоикрменет, первичный ключ |
| Code     | Varchar | Код валюты                                |
| FullName | Varchar | Полное имя валюты                         |
| Sign     | Varchar | Символ валюты                             |
#### Таблица ExchangeRates

| Колонка          | Тип        | Комментарий                                                 |
| ---------------- | ---------- | ----------------------------------------------------------- |
| ID               | int        | Айди курсе обмена, автоикремент, первичный ключ             |
| BaseCurrencyId   | int        | ID базовой валюты, внешний ключ на Currencies.id            |
| TargetCurrencyId | int        | ID целевой валюты, внешний ключ на Currencies.id            |
| Rate             | Decimal(6) | Курс обмена единицы базовой валюты к единице целевой валюты |


Как можно понять из параметров в application.properties как база данных была выбрана postgresql, при смене базы данных стоит указать в pom.xml другой драйвер для неё.

