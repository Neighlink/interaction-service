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

    // END DAYS

    @GetMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getOptionsByPoll(@PathVariable("pollId") Long pollId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<Option>> options = optionService.findAllByPoll(pollId);
            okResponse(options.get());
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/replies", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> saveReplyOption(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization, @RequestBody RequestOption requestOption) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            // TODO: VALIDAR SI EXISTE OPCION SELECCIONADA
            Optional<Option> option = optionService.findById(requestOption.getOptionId());
            if (option.isEmpty()) {
                notFoundResponse();
            } else {
                OptionResident optionResident = new OptionResident();
                optionResident.setDate(new Date());
                optionResident.setComment(requestOption.getComment());
                optionResident.setResidentId(authToken.getId());
                optionResident.setOption(option.get());
                OptionResident optionSaved = optionResidentService.save(optionResident);
                okResponse(optionSaved);
            }
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/condominiums/{condominiumId}/polls/{pollId}/replies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getAllReplyByPoll(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            ResponsePollResident responsePollResident = new ResponsePollResident();
            Optional<Poll> poll = pollService.findById(pollId);
            if (poll.isEmpty()) {
                notFoundResponse();
            }
            responsePollResident.setPoll(poll.get());
            Optional<List<Option>> options = optionService.findAllByPoll(pollId);
            List<OptionReplyModel> optionReplies = new ArrayList<>();
            for (Option option : options.get()) {
                OptionReplyModel optionReply = new OptionReplyModel();
                Optional<List<OptionResident>> optionResidents = optionResidentService.findAllByOption(option);
                optionReply.setOption(option);
                optionReply.setOptionResidents(optionResidents.get());
                optionReplies.add(optionReply);
            }
            responsePollResident.setOptionReplies(optionReplies);
            okResponse(responsePollResident);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }


    @GetMapping(path = "/condominiums/{condominiumId}/polls/{pollId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getDetailPoll(@PathVariable("condominiumId") Long condominiumId, @PathVariable("pollId") Long pollId, @RequestHeader String Authorization) {
        try {
            ResponseAuth authToken = authToken(Authorization);
            if (!authToken.isAuthorized()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<Poll> poll = pollService.findById(pollId);
            if (poll.isEmpty()) {
                notFoundResponse();
            }
            Optional<List<Option>> options = optionService.findAllByPoll(pollId);
            poll.get().setOptions(options.get());
            okResponse(poll);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

}
