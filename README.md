<h1><a href="https://github.com/DemonZhdb/SearchEngine" target="_blank">SearchEngine </a> -локальный поисковый движок </h1>

## Описание
Проект создания локальной поисковой системы, дающей возможность полнотекстового поиска по сайтам, указанным в конфигурационном файле.
Система содержит несколько контроллеров, сервисов и репозиторий подключенный к БД PostgreSQL.  
На стартой странице системы выводится статистическая информация ("DASHBOARD"), о проиндексированных сайтах и страницах, а также леммах (начальная словарная форма слова), содержащихся на этих страницах
 
![image](https://user-images.githubusercontent.com/95174795/179357444-35ffe3dc-5d14-434e-a993-16abaaf58ad6.png)
Система позволяет производить как полную индексацию всех страниц на сайтов из списка, так и добавление и переиндексацию отдельно заданных страниц этих сайтов.

![image](https://user-images.githubusercontent.com/95174795/179358222-8cf40602-56c3-437b-95d2-3042be2ca5b2.png)

В строку запроса для поиска можно вводить как одно слово, так и целую фразу. При этом  можно выбирать, где искать - на конкретном сайте или выбрать все сайты.

![image](https://user-images.githubusercontent.com/95174795/179360146-aceffb48-77b8-4787-8025-65a38f36ce6b.png)

В результате поиска выводится список наиболее релевантных страниц, где встречаются слова из строки запроса.


## Стек используемых технологий
Java Core, Spring Boot, JPA, Hibernate, JDBC, Security, PostgreSQL, REST API, JSOUP, Maven, Git <br>
Также  библиотеки лемматизации - RussianMorphology и стемминга (нахождения основы слова) - stemmer.

## Настройки для запуска
### Зависимости
Для успешного скачивания и подключения к проекту зависимостей из GitHub необходимо настроить Maven конфигурацию в файле `settings.xml`.

Для работы системы  в файле `pom.xml` необходимо добавить информацмию о фреймворке:
```
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.4</version>
</parent>
```
а также ссылку на репозиторий для скачивания зависимостей лемматизатора:
```
<repositories>
        <repository>
            <id>github</id>
            <name>GitHub Apache Maven Packages - Russian Morphology</name>
            <url>https://maven.pkg.github.com/skillbox-java/russianmorphology</url>
        </repository>
    </repositories>

````
Также нужно указать подключение следующих зависимостей apache Maven:

```
 spring-boot-starter-security
 spring-boot-starter-thymeleaf
 spring-boot-starter-web
 spring-boot-starter-data-jpa
 postgresql
 jsoup
```
Для подключения зависимостей `morph, morphology, dictionary-reader, english, russian`из источника : `org.apache.lucene.morphology` необходимо ещё создать (либо отредактировать если он имеется -  в  Windows он располагается в директории C:/Users/<Имя вашего пользователя>/.m2) файл settings.xml, в котором указать токен  для получения данных из публичного репозитория. В файл нужно внести следующие строки:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
 https://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>github</id>
            <configuration>
                <httpHeaders>
                    <property>
                        <name>Authorization</name>
                        <value>Bearer 
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</value>
                    </property>
                </httpHeaders>
            </configuration>
        </server>
    </servers>
</settings>
```
## Запуск
Стартовая страница поискового движка находится по адресу : http://localhost:8080/ <br>
Сразу при старте система запрашивает логин/пароль, которые указаны в файле конфигурации `src/resources/application.yml`:
```
 security:
    user:
      name: user
      password: user
      roles: user

```
