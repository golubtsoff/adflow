package rest.statistics;

import com.google.gson.Gson;
import entity.statistics.Request;
import entity.statistics.Viewer;
import entity.users.PictureFormat;
import entity.users.customer.Campaign;
import entity.users.customer.Picture;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
import exception.BadRequestException;
import exception.ConflictException;
import exception.NotFoundException;
import service.RequestService;
import util.JsonHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/session")
@PlatformSecure
public class RequestResource {

    public static final String PATH_TO_FILE = "http://app4pro.ru/pictures/";

    @Context
    private HttpHeaders headers;

    public class UpdateRequestDto{
        private boolean confirmShow;
        private boolean clickOn;

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
        private String pathOnClick;
        private int durationShow;

        public InitialResponseDto(Request request) {
            this.sessionId = request.getSession().getId();
            this.requestId = request.getId();
            this.urlForLoadFile = getUrlToPicture(request);
            this.pathOnClick = request.getCampaign().getPathOnClick();
            this.durationShow = request.getDurationShow();
        }

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

        public String getPathOnClick() {
            return pathOnClick;
        }

        public void setPathOnClick(String pathOnClick) {
            this.pathOnClick = pathOnClick;
        }

        public int getDurationShow() {
            return durationShow;
        }

        public void setDurationShow(int durationShow) {
            this.durationShow = durationShow;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content){
        try{
            Gson gson = JsonHelper.getGson();
            Viewer viewer = gson.fromJson(content, Viewer.class);
            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            Request request = RequestService.create(platformId, viewer);
            if (request == null)
                throw new Exception();

            return Response.ok(JsonHelper.getGson().toJson(new InitialResponseDto(request))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static String getUrlToPicture(Request request){
        Set<Picture> pictures = request.getCampaign().getPictures();
        PictureFormat pictureFormat = request.getSession().getPlatform().getPictureFormat();
        for (Picture picture : pictures){
            if (picture.getPictureFormat().equals(pictureFormat)){
                return PATH_TO_FILE + picture.getFileName();
            }
        }
        return null;
    }

    @POST
    @Path("{sid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("sid") long sessionId){
        try{
            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            Request request = RequestService.create(platformId, sessionId);
            if (request == null)
                throw new Exception();

            return Response.ok(JsonHelper.getGson().toJson(new InitialResponseDto(request))).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
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
            if (updateRequestDto == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            RequestService.update(platformId, requestId, updateRequestDto);

            return Response.noContent().build();
        } catch (BadRequestException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
