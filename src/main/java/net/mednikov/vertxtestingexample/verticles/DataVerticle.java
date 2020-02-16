package net.mednikov.vertxtestingexample.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import net.mednikov.vertxtestingexample.models.Person;
import net.mednikov.vertxtestingexample.models.PersonMapper;
import net.mednikov.vertxtestingexample.repositories.PersonRepository;

import static net.mednikov.vertxtestingexample.verticles.Routes.*;

import java.util.Optional;

public class DataVerticle extends AbstractVerticle {

    private final PersonRepository repository;

    public DataVerticle(PersonRepository repository){
        this.repository = repository;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(DATA_ADD_NEW_PERSON, this::addNewPerson);
        eventBus.consumer(DATA_FIND_ONE, this::findOnePerson);
        startPromise.complete();
    }

    private void addNewPerson (Message<Object> message){
        JsonObject payload = JsonObject.mapFrom(message.body());
        Person person = PersonMapper.fromJson(payload);
        Person result = repository.add(person);
        JsonObject body = JsonObject.mapFrom(result);
        message.reply(body);
    }

    private void findOnePerson(Message<Object> message){
        JsonObject payload = JsonObject.mapFrom(message.body());
        String id = payload.getString("id");
        Optional<Person> result = repository.findById(id);
        result.ifPresentOrElse(res -> {
            JsonObject body = JsonObject.mapFrom(res);
            message.reply(body);
        }, () -> message.fail(404, "Not found"));
    }

}