# Домашнее задание к занятию «2.2. Dependency Lookup, Dependency Injection, IoC, Spring, Application Context»

В качестве результата пришлите ссылки на ваши GitHub-проекты в личном кабинете студента на сайте [netology.ru](https://netology.ru).

**Важно**: ознакомьтесь со ссылками, представленными на главной странице [репозитория с домашними заданиями](../README.md).

**Важно**: если у вас что-то не получилось, то оформляйте Issue [по установленным правилам](../report-requirements.md).

## Как сдавать задачи

1. Возьмите проект с предыдущей лекции
1. Создайте в нём ветки `feature/di-annotation` и `feature/di-java`, в которых и реализуйте соответствующую функциональность
1. Сделайте пуш (удостоверьтесь, что ваш код появился на GitHub) и создайте Pull Request'ы из обеих веток
1. Ссылку на Pull Request'ы отправьте в личном кабинете на сайте [netology.ru](https://netology.ru)

## DI

### Легенда

В рамках лекции мы посмотрели, как использовать Spring для связывания зависимостей.

Соответственно, возникает вопрос, почему бы его не использовать в вашем приложении с сервлетами и не заменить указанный ниже код на DI со Spring:
```java
@Override
public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
}
```

### Задача

Замените код в методе `init` на DI со Spring с использованием следующих методов конфигурирования бинов:
1. Annotation Config (ветка `feature/di-annotation`)
1. Java Config (ветка `feature/di-java`)

Обратите внимание, что вся функциональность (CRUD), реализованная до этого, должна по-прежнему работать.

### Результат

В качестве результата пришлите ссылку на ваши Pull Request'ы в личном кабинете студента на сайте [netology.ru](https://netology.ru).
