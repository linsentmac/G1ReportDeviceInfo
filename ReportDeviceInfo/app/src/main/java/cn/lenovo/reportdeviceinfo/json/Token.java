package cn.lenovo.reportdeviceinfo.json;

import java.util.List;

/**
 * @Date 2018-08-22.
 */
public class Token {

    /**
     * access_token : eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXZlbG9wZXJJZCI6MTM4LCJwZXJtaXNzaW9ucyI6WyIxIiwiMiIsIjMiLCI0IiwiNSJdLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0ZSJdLCJhcHBJZCI6NDIyLCJleHAiOjE1MzU1MDU1NTgsImF1dGhvcml0aWVzIjpbIlJPTEVfQVBQIl0sImp0aSI6IjY1MTJiNzM1LWM4NmMtNGNiOC04Njc5LTQ3OGQ4YTQwYjRiYSIsImNsaWVudF9pZCI6ImFwcC84NEFENUFDMEVDOEE0MEEyODBGRjBDRjI4REQzNzI4QSJ9.sJyxu1H4qt8k9sDkl5ffJzhwd4l4qQoq3TZcVmukN7h7kIWqmcKs1ZMDkXscbZmtr7TmUiHAuVNk2jqgDZepXQeT1OotR0mYAjd81PWwRya7z0Lcepan83auM0nivgG79D6Ao4wHH0kiho36nVwJKxc-eAxS1WpWVQqcwYaTRInysh3sOG5iSXAq_TnbZtkSzJtKgAcZgarOjX3NxEZtiQbtO7pjKD7j6_gb2JZpm6JBZ4lQyQs4ln01VGW1BQjnXBX8m3m3BqzqGetDF-GyHJV_ELFlfOHxF1XonpIsAipnApGDMvLIJgayofSM7NuOiCvGf3fbfci6udwwbh22-A
     * token_type : bearer
     * expires_in : 604799
     * scope : trust read write
     * developerId : 138
     * permissions : ["1","2","3","4","5"]
     * appId : 422
     * jti : 6512b735-c86c-4cb8-8679-478d8a40b4ba
     */

    public String access_token;
    public String token_type;
    public int expires_in;
    public String scope;
    public int developerId;
    public int appId;
    public String jti;
    public List<String> permissions;
}
