package rest.statistics;

import com.google.gson.Gson;
import entity.statistics.Viewer;
import entity.users.partner.PlatformToken;
import exception.ConflictException;
import exception.NotFoundException;
import service.RequestService;
import util.JsonHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Platform
public class RequestResource {

    @Context
    private HttpHeaders headers;

    public class InitialRequestDto{
        private Viewer viewer;

        public Viewer getViewer() {
            return viewer;
        }

        public void setViewer(Viewer viewer) {
            this.viewer = viewer;
        }
    }

    public class UpdateRequestDto{
        private Long requestId;
        private boolean confirmShow;
        private boolean clickOn;

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public boolean isConfirmShow() {
            return confirmShow;
        }

        public void setConfirmShow(boolean confirmShow) {
            this.confirmShow = confirmShow;
        }

        public boolean isClickOn() {
            return clickOn;
        }

        public void setClickOn(boolean clickOn) {
            this.clickOn = clickOn;
        }
    }

    public class InitialResponseDto{
        private Long sessionId;
        private Long requestId;
        private String urlForLoadFile;

        public Long getSessionId() {
            return sessionId;
        }

        public void setSessionId(Long sessionId) {
            this.sessionId = sessionId;
        }

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public String getUrlForLoadFile() {
            return urlForLoadFile;
        }

        public void setUrlForLoadFile(String urlForLoadFile) {
            this.urlForLoadFile = urlForLoadFile;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content){
        try{
            Gson gson = JsonHelper.getGson();
            InitialRequestDto initialRequestDto = gson.fromJson(content, InitialRequestDto.class);

            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            InitialResponseDto initialResponseDto = RequestService.create(platformId, initialRequestDto);
            if (initialResponseDto == null)
                throw new Exception();

            return Response.ok(gson.toJson(initialResponseDto)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("{sid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("sid") long sessionId){
        try{
            Gson gson = JsonHelper.getGson();
            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            InitialResponseDto initialResponseDto = RequestService.create(platformId, sessionId);
            if (initialResponseDto == null)
                throw new Exception();

            return Response.ok(gson.toJson(initialResponseDto)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("request/{rid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("rid") long requestId, String content){
        try{
            Gson gson = JsonHelper.getGson();
            UpdateRequestDto updateRequestDto = gson.fromJson(content, UpdateRequestDto.class);

            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            RequestService.update(platformId, requestId, updateRequestDto);

            return Response.noContent().build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
