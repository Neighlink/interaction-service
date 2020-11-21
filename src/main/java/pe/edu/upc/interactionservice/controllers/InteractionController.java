package pe.edu.upc.interactionservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.interactionservice.entities.News;
import pe.edu.upc.interactionservice.entities.Option;
import pe.edu.upc.interactionservice.entities.OptionResident;
import pe.edu.upc.interactionservice.entities.Poll;
import pe.edu.upc.interactionservice.models.*;
import pe.edu.upc.interactionservice.services.NewsService;
import pe.edu.upc.interactionservice.services.OptionResidentService;
import pe.edu.upc.interactionservice.services.OptionService;
import pe.edu.upc.interactionservice.services.PollService;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    InteractionController() {
        response = new Response();
        responseAuth = new ResponseAuth();
    }

    @Autowired
    private PollService pollService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private OptionService optionService;
    @Autowired
    private OptionResidentService optionResidentService;

    private final static String URL_PROFILE = "http://localhost:8092/profiles";
    private final static Logger LOGGER = Logger.getLogger("bitacora.subnivel.Control");
    HttpStatus status;

    Response response = new Response();
    ResponseAuth responseAuth = new ResponseAuth();

    private ResponseAuth authToken(String token) {
        try {
            var values = new HashMap<String, String>() {{
            }};
            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(values);
            String url = URL_PROFILE + "/authToken";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .setHeader("Authorization", token)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject responseAPI = new JSONObject(response.body());
            var status = responseAPI.getInt("status");
            if (status != 200) {
                var message = responseAPI.getString("message");
                responseAuth.initError(false, message);
                return responseAuth;
            }
            JSONObject result = responseAPI.getJSONObject("result");
            responseAuth.init(result.getLong("id"), result.getString("userType"), result.getBoolean("authorized"), "");
            return responseAuth;
        } catch (Exception e) {
            responseAuth.initError(false, e.getMessage());
            return responseAuth;
        }
    }

    public void unauthorizedResponse() {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("UNAUTHORIZED USER");
        status = HttpStatus.UNAUTHORIZED;
    }

    public void notFoundResponse() {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage("ENTITY NOT FOUND");
        status = HttpStatus.NOT_FOUND;
    }

    public void okResponse(Object result) {
        response.setStatus(HttpStatus.OK.value());
        response.setResult(result);
        response.setMessage("SERVICE SUCCESS");
        status = HttpStatus.OK;
    }

    public void conflictResponse(String message) {
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(message);
        status = HttpStatus.CONFLICT;
    }

    public void internalServerErrorResponse(String message) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + " => " + message);
    }

    // START NEWS
    @GetMapping(path = "/condominiums/{id}/news", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getNewsByCondominium(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<News>> news = newsService.findAllByCondominiumId(id);
            okResponse(news.get());
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums/{id}/news", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addNewsByCondominium(@PathVariable("id") Long id, @RequestHeader String Authorization, @RequestBody RequestNews requestNews) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            News news = new News();
            news.setCondominiumId(id);
            news.setDate(new Date());
            news.setDescription(requestNews.getDescription());
            news.setTitle(requestNews.getTitle());
            News newsSaved = newsService.save(news);
            okResponse(newsSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PutMapping(path = "/condominiums/{id}/news/{newsId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getNewsByCondominium(@PathVariable("id") Long id, @PathVariable("newsId") Long newsId, @RequestHeader String Authorization, @RequestBody RequestNews requestNews) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<News> news = newsService.findById(newsId);
            if (news.isEmpty()) {
                notFoundResponse();
            } else {
                news.get().setTitle(requestNews.getTitle());
                news.get().setDescription(requestNews.getDescription());
                News newsSaved = newsService.save(news.get());
                okResponse(newsSaved);
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @DeleteMapping(path = "/condominiums/{id}/news/{newsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteNews(@PathVariable("id") Long id, @PathVariable("newsId") Long newsId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<News> news = newsService.findById(newsId);
            if (news.isEmpty()) {
                notFoundResponse();
            } else {
                newsService.deleteById(newsId);
                okResponse(null);
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    // END NEWS

    // START POLL

    @GetMapping(path = "/condominiums/{condominiumId}/polls", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getPolls(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<Poll>> polls = pollService.findAllByCondominiumId(condominiumId);
            if (polls.isEmpty()) {
                okResponse(new ArrayList<>());
            } else {
                if (authToken.getUserType().equals("ADM")) {
                    okResponse(polls.get());
                } else {
                    var actualDate = new Date();
                    var pollsValid = new ArrayList<Poll>();
                    for (Poll poll : polls.get()) {
                        if (poll.getStartDate().getTime() <= actualDate.getTime() && actualDate.getTime() <= poll.getEndDate().getTime()) {
                            pollsValid.add(poll);
                        }
                    }
                    okResponse(pollsValid);
                }
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums/{condominiumId}/polls", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addPoll(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization, @RequestBody RequestPoll requestPoll) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            Poll poll = new Poll();
            poll.setTitle(requestPoll.getTitle());
            poll.setDescription(requestPoll.getDescription());
            poll.setStartDate(requestPoll.getStartDate());
            poll.setEndDate(requestPoll.getEndDate());
            poll.setAdministratorId(authToken.getId());
            poll.setDelete(false);
            poll.setCondominiumId(condominiumId);
            Poll pollSaved = pollService.save(poll);
            var options = new ArrayList<Option>();
            for (Option option : requestPoll.getOptions()) {
                Option newOption = new Option();
                newOption.setName(option.getName());
                newOption.setDescription(option.getDescription());
                newOption.setPollId(pollSaved.getId());
                Option optionSaved = optionService.save(newOption);
                options.add(optionSaved);
            }
            pollSaved.setOptions(options);
            okResponse(pollSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PutMapping(path = "/condominiums/{condominiumId}/polls/{pollId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updatePoll(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization, @RequestBody RequestPoll requestPoll) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            var poll = pollService.findById(pollId);
            if (poll.isEmpty()) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }
            poll.get().setTitle(requestPoll.getTitle());
            poll.get().setDescription(requestPoll.getDescription());
            poll.get().setStartDate(requestPoll.getStartDate());
            poll.get().setEndDate(requestPoll.getEndDate());
            Poll pollSaved = pollService.save(poll.get());
            var options = new ArrayList<Option>();
            for (Option option : requestPoll.getOptions()) {
                if (option.getId() != null) {
                    var findOption = optionService.findById(option.getId());
                    if (!findOption.isEmpty()) {
                        findOption.get().setName(option.getName());
                        findOption.get().setDescription(option.getDescription());
                        Option optionSaved = optionService.save(findOption.get());
                        options.add(optionSaved);
                    }
                } else {
                    Option newOption = new Option();
                    newOption.setName(option.getName());
                    newOption.setDescription(option.getDescription());
                    newOption.setPollId(pollSaved.getId());
                    Option optionSaved = optionService.save(newOption);
                    options.add(optionSaved);
                }
            }
            pollSaved.setOptions(options);
            okResponse(pollSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @DeleteMapping(path = "/condominiums/{condominiumId}/polls/{pollId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deletePoll(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            var poll = pollService.findById(pollId);
            if (poll.isEmpty()) {
                notFoundResponse();
            } else {
                poll.get().setDelete(true);
                pollService.save(poll.get());
                okResponse(null);
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }
    // END POLL

    // START RESPONSE POLL RESIDENT
    @GetMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/responses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getPollsResponseResident(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            var poll = pollService.findById(pollId);
            if (poll.isEmpty()) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }

            var options = optionService.findAllByPoll(pollId);
            if (options.isEmpty()) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }

            var responsePoll = new ResponsePollResident();
            responsePoll.setPoll(poll.get());
            var optionReplies = new ArrayList<OptionReplyModel>();
            for (Option option : options.get()) {
                var optionReply = new OptionReplyModel();
                var optionsResident = optionResidentService.findAllByOption(option.getId());
                optionReply.setOption(option);
                optionReply.setOptionResidents(optionsResident);
                optionReplies.add(optionReply);
            }
            responsePoll.setOptionReplies(optionReplies);
            okResponse(responsePoll);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/responses", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addResponseResident(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization, @RequestBody RequestOptionResident requestOptionResident) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            OptionResident optionResident = new OptionResident();
            optionResident.setDate(new Date());
            optionResident.setComment(requestOptionResident.getComment());
            optionResident.setOptionId(requestOptionResident.getOptionId());
            optionResident.setDelete(false);
            optionResident.setResidentId(authToken.getId());
            var optionResidentSaved = optionResidentService.save(optionResident);
            okResponse(optionResidentSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }


    @DeleteMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/responses/{responseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteResponseResident(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @PathVariable("responseId") Long responseId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            var optionResident = optionResidentService.findById(responseId);
            if (optionResident.isEmpty()) {
                notFoundResponse();

            } else {
                optionResident.get().setDelete(true);
                optionResidentService.save(optionResident.get());
                okResponse(null);
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }
    // END RESPONSE POLL RESIDENT

}
