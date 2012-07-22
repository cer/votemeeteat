require 'sinatra'

post '/' do
  phone_number = params[:From]
  registration_url = "http://registration-votemeeteat.cloudfoundry.com/register.html?phoneNumber=#{URI.encode(phone_number, "+")}"
  <<-eof
	<Response>
		<Sms>To complete registration please go to #{registration_url}
	    </Sms>
	</Response>  
eof
end
