package rest.statistics;

import com.google.gson.Gson;
import entity.statistics.Request;
import entity.statistics.Viewer;
import entity.users.partner.PlatformToken;
import exception.ConflictException;
import exception.NotFoundException;
import service.RequestService;
import util.JsonHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/request")
@PlatformSecure
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
        private Long requestId;
        private String urlForLoadFile;
        private String pathOnClick;
        private int durationShow;

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
            InitialRequestDto initialRequestDto = null;
            long platformId = Long.valueOf(headers.getHeaderString(PlatformToken.PID));
            InitialResponseDto initialResponseDto = RequestService.create(
                    platformId,
                    initialRequestDto,
                    new InitialResponseDto()
            );
            if (initialResponseDto == null)
                throw new Exception();
            return Response.ok(JsonHelper.getGson().toJson(initialResponseDto)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{rid}")
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
